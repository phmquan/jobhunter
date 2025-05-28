from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import numpy as np

from vector_db import initialize_index, add_embeddings_to_index, search_index
from embedding import initialize_embedding_model, get_embedding, embedding_model as global_embedding_model
from generator import generate_response

app = FastAPI()

class JobRequirement(BaseModel):
    id: int  # Changed from str to int to match Long in Java
    name: str  # Changed from title to name to match Spring Boot
    level: str  # e.g., "Junior", "Mid-level", "Senior"
    description: str

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
        embedding = generate_embedding(job.description)
        
        # Add to vector database
        add_embeddings_to_index(str(job.id), embedding, job.description)
        
        return {"message": "Job requirement embedded successfully", "id": job.id}
    except Exception as e:
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
                "index": int(doc["index"])  # Convert numpy.int64 to Python int
            })
        
        if not processed_docs:
             context = "No relevant documents found."
        else:
             doc_ids = [doc['doc_id'] for doc in processed_docs if doc['doc_id'] is not None]
             context = f"Relevant document IDs: {', '.join(doc_ids)}. (Actual content retrieval needed here)"

        prompt = f"You are a career coach. Base on the context provider, Analyze the candidate's CV against the software developer job requirements and provide specific advice on skill gaps, strengths, and improvements.:\n\nContext: {context}\n\nQuestion: {query.query_text}\n\nAnswer:"
        
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