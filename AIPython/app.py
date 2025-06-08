from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import numpy as np

from vector_db import initialize_index, add_embeddings_to_index, search_index
from embedding import initialize_embedding_model, get_embedding, embedding_model as global_embedding_model
from generator import generate_response

app = FastAPI()

class Company(BaseModel):
    id: str

class Skill(BaseModel):
    id: str

class JobRequirement(BaseModel):
    id: Optional[int] = None  # Made optional since it might not be sent from Java
    name: str
    location: Optional[str] = None
    salary: Optional[str] = None
    quantity: Optional[str] = None
    level: str
    description: str
    company: Optional[Company] = None
    skills: Optional[List[Skill]] = None

class Query(BaseModel):
    query_text: str
    top_k: int = 5

# Initialize the embedding model and FAISS index on startup
@app.on_event("startup")
async def startup_event():
    try:
        initialize_embedding_model()
        # Get the dimension from the initialized model
        if global_embedding_model and hasattr(global_embedding_model, 'get_sentence_embedding_dimension'):
             dimension = global_embedding_model.get_sentence_embedding_dimension()
        elif global_embedding_model and hasattr(global_embedding_model, 'get_word_embedding_dimension'): # Fallback for older versions/models
             dimension = global_embedding_model.get_word_embedding_dimension()
        else:
             # If neither method works, try encoding a sample sentence to get dimension
             try:
                 sample_embedding = get_embedding("sample text")
                 dimension = sample_embedding.shape[0]
                 print(f"Determined embedding dimension from sample: {dimension}")
             except Exception as e:
                 print(f"Could not determine embedding dimension: {e}")
                 raise ValueError("Could not determine embedding dimension.") from e

        initialize_index(dimension)
    except Exception as e:
        print(f"Failed to initialize RAG components: {e}")
        # Depending on severity, you might want to exit or set a flag

@app.post("/embed-job-requirement/")
async def embed_job_requirement(job: JobRequirement):
    """Embeds a job requirement and stores it in the vector database."""
    try:
        # Generate embedding for the job description
        embedding = get_embedding(job.description)
        
        # Use a default ID if none provided
        doc_id = str(job.id) if job.id is not None else "temp_" + str(hash(job.description))
        
        # Add to vector database
        add_embeddings_to_index(doc_id, embedding, job.description)
        
        return {"message": "Job requirement embedded successfully", "id": doc_id}
    except Exception as e:
        print(f"Error in embed_job_requirement: {str(e)}")  # Add logging
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/generate-response/")
async def generate_rag_response(query: Query):
    """API to generate response based on query using RAG."""
    try:
        query_embedding = get_embedding(query.query_text)
        relevant_docs_info = search_index(query_embedding, query.top_k)
        
        # Convert numpy types to Python native types for JSON serialization
        processed_docs = []
        for doc in relevant_docs_info:
            processed_docs.append({
                "doc_id": str(doc["doc_id"]) if doc["doc_id"] is not None else None,  # Convert to string
                "distance": float(doc["distance"]),  # Convert numpy.float32 to Python float
                "index": int(doc["index"]),  # Convert numpy.int64 to Python int
                "text": doc.get("text", "Text not found")  # Include the actual job requirement text
            })
        
        if not processed_docs:
             context = "No relevant documents found."
        else:
             # Include both IDs and texts in the context
             context_parts = []
             for doc in processed_docs:
                 if doc["doc_id"] is not None:
                     context_parts.append(f"Job ID {doc['doc_id']}: {doc['text']}")
             context = "\n".join(context_parts)

        prompt = f"You are a career coach. Based on the context provided, analyze the candidate's CV against the software developer job requirements and provide specific advice on skill gaps, strengths, and improvements:\n\nContext: {context}\n\nQuestion: {query.query_text}\n\nAnswer:"
        
        response_text = generate_response(prompt)
        
        return {
            "query": query.query_text,
            "relevant_docs": processed_docs,
            "response": response_text
        }
        
    except ValueError as ve:
         raise HTTPException(status_code=400, detail=str(ve))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating response: {e}") 