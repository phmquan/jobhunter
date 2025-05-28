from sentence_transformers import SentenceTransformer
import numpy as np

# Choose a pre-trained model
# You might want to use a different model depending on your specific needs
embedding_model = None
default_model_name = 'all-MiniLM-L6-v2'

def initialize_embedding_model(model_name: str = default_model_name):
    """Initializes the sentence transformer model."""
    global embedding_model
    try:
        embedding_model = SentenceTransformer(model_name)
        print(f"Embedding model {model_name} loaded successfully.")
    except Exception as e:
        print(f"Error loading embedding model {model_name}: {e}")
        raise

def get_embedding(text: str) -> np.ndarray:
    """Generates an embedding for the given text."""
    global embedding_model
    if embedding_model is None:
        raise ValueError("Embedding model not initialized.")
    
    # The model expects a list of strings
    embeddings = embedding_model.encode([text])
    return embeddings[0] 