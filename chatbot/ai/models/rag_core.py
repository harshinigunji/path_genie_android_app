import os
import json
import subprocess
import faiss
import numpy as np
from sentence_transformers import SentenceTransformer

# -------------------------
# Paths
# -------------------------
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../"))

VECTOR_DIR = os.path.join(BASE_DIR, "ai/vector_store/faiss_index")
DOCS_DIR = os.path.join(BASE_DIR, "ai/knowledge/docs")

MODEL_NAME = "all-MiniLM-L6-v2"
OLLAMA_MODEL = "tinyllama"

# Force offline
os.environ["TRANSFORMERS_OFFLINE"] = "1"
os.environ["HF_DATASETS_OFFLINE"] = "1"

# -------------------------
# Load models once
# -------------------------
embedding_model = SentenceTransformer(MODEL_NAME)

faiss_index = faiss.read_index(os.path.join(VECTOR_DIR, "index.faiss"))
with open(os.path.join(VECTOR_DIR, "doc_ids.json"), "r") as f:
    doc_ids = json.load(f)

docs = {}
for file in os.listdir(DOCS_DIR):
    if file.endswith(".json"):
        with open(os.path.join(DOCS_DIR, file), "r", encoding="utf-8") as f:
            d = json.load(f)
            docs[d["id"]] = d["content"]

# -------------------------
# Retrieval
# -------------------------
def retrieve_context(query, top_k=3):
    query_vec = embedding_model.encode([query]).astype("float32")
    _, indices = faiss_index.search(query_vec, top_k)

    chunks = []
    for idx in indices[0]:
        chunks.append(docs.get(doc_ids[idx], ""))

    return "\n\n".join(chunks)

# -------------------------
# Ollama call
# -------------------------
def ask_ollama(prompt):
    try:
        # Check if model exists first (optional, but good for debugging)
        # For now, just try to run it and catch errors
        result = subprocess.run(
            ["ollama", "run", OLLAMA_MODEL],
            input=prompt,
            text=True,
            encoding="utf-8",
            errors="ignore",
            capture_output=True
        )
        
        if result.returncode != 0:
            print(f"Ollama Error: {result.stderr}")
            return "Error: AI Model issue. " + result.stderr[:50]

        output = result.stdout.strip()
        if not output:
             # Fallback if model runs but returns nothing
            return "Error: No response from AI model. Is 'mistral' installed?"
            
        return output

    except Exception as e:
        print(f"Subprocess Error: {e}")
        return "Error: Failed to execute AI component."
