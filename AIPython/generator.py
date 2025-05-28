import requests
import json
import time

OLLAMA_API_URL = "http://localhost:11434/api/generate"
MAX_RETRIES = 3
RETRY_DELAY = 2  # seconds
TIMEOUT = 120  # increased timeout to 120 seconds

def check_ollama_connection():
    """Check if Ollama API is accessible"""
    try:
        response = requests.get("http://localhost:11434/api/tags", timeout=10)
        return response.status_code == 200
    except requests.exceptions.ConnectionError:
        return False
    except requests.exceptions.Timeout:
        return False

def generate_response(prompt: str, model: str = "hf.co/hoanghuy100202/FineTune_LLama_3B:latest") -> str:
    """Calls the local Ollama API to generate a response."""
    if not check_ollama_connection():
        return "Error: Ollama API is not accessible. Please ensure Ollama is running on your system."
    
    payload = {
        "model": model,
        "prompt": prompt,
        "stream": False
    }
    
    for attempt in range(MAX_RETRIES):
        try:
            print(f"Attempting to generate response (attempt {attempt + 1}/{MAX_RETRIES})...")
            response = requests.post(OLLAMA_API_URL, json=payload, timeout=TIMEOUT)
            response.raise_for_status()
            
            result = response.json()
            return result.get('response', 'Error: No response text found.')
            
        except requests.exceptions.Timeout:
            if attempt < MAX_RETRIES - 1:
                print(f"Attempt {attempt + 1} timed out after {TIMEOUT} seconds. Retrying in {RETRY_DELAY} seconds...")
                time.sleep(RETRY_DELAY)
            else:
                return f"Error: Request timed out after {TIMEOUT} seconds. The model might be too large or the system might be overloaded. Try using a smaller model or increasing system resources."
                
        except requests.exceptions.RequestException as e:
            if attempt < MAX_RETRIES - 1:
                print(f"Attempt {attempt + 1} failed: {e}. Retrying in {RETRY_DELAY} seconds...")
                time.sleep(RETRY_DELAY)
            else:
                error_msg = f"Error: Failed to connect to Ollama API after {MAX_RETRIES} attempts. "
                if "404" in str(e):
                    error_msg += "The model might not be available. Please check if the model is pulled: 'ollama pull hf.co/hoanghuy100202/FineTune_LLama_3B:latest'"
                elif "Connection refused" in str(e):
                    error_msg += "Ollama service might not be running. Please start Ollama."
                else:
                    error_msg += f"Error details: {e}"
                return error_msg
                
        except json.JSONDecodeError:
            return "Error: Invalid JSON response from Ollama API." 