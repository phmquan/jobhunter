import faiss
import numpy as np
import os
import pickle
from pathlib import Path

# Directory to store the vector database files
DB_DIR = "vector_db"
INDEX_FILE = os.path.join(DB_DIR, "faiss_index.bin")
MAPPING_FILE = os.path.join(DB_DIR, "doc_mappings.pkl")
TEXT_FILE = os.path.join(DB_DIR, "job_texts.pkl")

# In-memory FAISS index and mapping
index = None
doc_id_to_index = {}
index_to_doc_id = []
job_texts = {}  # Store job descriptions by doc_id

def ensure_db_dir():
    """Ensure the database directory exists"""
    Path(DB_DIR).mkdir(parents=True, exist_ok=True)

def save_index():
    """Save the FAISS index and mappings to disk"""
    if index is None:
        return
    
    ensure_db_dir()
    # Save FAISS index
    faiss.write_index(index, INDEX_FILE)
    # Save mappings
    with open(MAPPING_FILE, 'wb') as f:
        pickle.dump({
            'doc_id_to_index': doc_id_to_index,
            'index_to_doc_id': index_to_doc_id
        }, f)
    # Save job texts
    with open(TEXT_FILE, 'wb') as f:
        pickle.dump(job_texts, f)
    print(f"Vector database saved to {DB_DIR}")

def load_index():
    """Load the FAISS index and mappings from disk"""
    global index, doc_id_to_index, index_to_doc_id, job_texts
    
    if not os.path.exists(INDEX_FILE) or not os.path.exists(MAPPING_FILE) or not os.path.exists(TEXT_FILE):
        print("No existing vector database found")
        return False
    
    try:
        # Load FAISS index
        index = faiss.read_index(INDEX_FILE)
        # Load mappings
        with open(MAPPING_FILE, 'rb') as f:
            mappings = pickle.load(f)
            doc_id_to_index = mappings['doc_id_to_index']
            index_to_doc_id = mappings['index_to_doc_id']
        # Load job texts
        with open(TEXT_FILE, 'rb') as f:
            job_texts = pickle.load(f)
        print(f"Vector database loaded from {DB_DIR}")
        return True
    except Exception as e:
        print(f"Error loading vector database: {e}")
        return False

def initialize_index(dimension: int):
    """Initializes the FAISS index with a given dimension."""
    global index, index_to_doc_id, job_texts
    # Try to load existing index first
    if load_index():
        return
    
    # If no existing index, create new one
    index = faiss.IndexFlatL2(dimension)
    index_to_doc_id = []
    job_texts = {}
    print(f"New FAISS index initialized with dimension {dimension}")

def add_embeddings_to_index(doc_id: str, embedding: np.ndarray, job_text: str):
    """Adds a document embedding and text to the FAISS index."""
    global index, doc_id_to_index, index_to_doc_id, job_texts
    if index is None:
        raise ValueError("FAISS index not initialized.")
        
    # Check if doc_id already exists, if so, update (simple approach: remove and re-add)
    if doc_id in doc_id_to_index:
        old_index = doc_id_to_index[doc_id]
        print(f"Warning: doc_id {doc_id} already exists. Adding new vector.")
        
    embedding = embedding.astype('float32').reshape(1, -1)
    index.add(embedding)
    new_index = index.ntotal - 1
    doc_id_to_index[doc_id] = new_index
    index_to_doc_id.append(doc_id)
    job_texts[doc_id] = job_text  # Store the job text
    print(f"Added embedding and text for doc_id {doc_id} at index {new_index}")
    
    # Save the updated index and mappings
    save_index()

def search_index(query_embedding: np.ndarray, k: int = 5):
    """Searches the FAISS index for the top k most similar documents."""
    global index, index_to_doc_id, job_texts
    if index is None:
        raise ValueError("FAISS index not initialized.")

    query_embedding = query_embedding.astype('float32').reshape(1, -1)
    distances, indices = index.search(query_embedding, k)

    results = []
    for i in range(len(indices[0])):
        idx = indices[0][i]
        if idx != -1: # -1 means no result found
            try:
                doc_id = index_to_doc_id[idx]
                results.append({
                    "doc_id": doc_id,
                    "distance": float(distances[0][i]),
                    "index": int(idx),
                    "text": job_texts.get(doc_id, "Text not found")  # Include the job text in results
                })
            except IndexError:
                print(f"Warning: Index {idx} found in search results but no corresponding doc_id in map.")
                results.append({
                    "doc_id": None,
                    "distance": float(distances[0][i]),
                    "index": int(idx),
                    "text": "Text not found"
                })

    print(f"Search complete. Found {len(results)} results.")
    return results 