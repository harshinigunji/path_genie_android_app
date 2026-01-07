import os
import json
import sys
import faiss
import numpy as np
from sentence_transformers import SentenceTransformer

# Fix project root path
sys.path.append(
    os.path.abspath(os.path.join(os.path.dirname(__file__), "../../"))
)

DOCS_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../knowledge/docs")
)

VECTOR_DIR = os.path.join(os.path.dirname(__file__), "faiss_index")

MODEL_NAME = "all-MiniLM-L6-v2"

def load_documents():
    texts = []
    ids = []

    for file in os.listdir(DOCS_DIR):
        if file.endswith(".json"):
            with open(os.path.join(DOCS_DIR, file), "r", encoding="utf-8") as f:
                doc = json.load(f)
                texts.append(doc["content"])
                ids.append(doc["id"])

    return texts, ids

def build_faiss():
    print("Loading embedding model...")
    model = SentenceTransformer(MODEL_NAME)

    print("Loading documents...")
    texts, ids = load_documents()

    print("Creating embeddings...")
    embeddings = model.encode(texts, show_progress_bar=True)

    embeddings = np.array(embeddings).astype("float32")

    dimension = embeddings.shape[1]
    index = faiss.IndexFlatL2(dimension)
    index.add(embeddings)

    os.makedirs(VECTOR_DIR, exist_ok=True)
    faiss.write_index(index, os.path.join(VECTOR_DIR, "index.faiss"))

    with open(os.path.join(VECTOR_DIR, "doc_ids.json"), "w") as f:
        json.dump(ids, f)

    print("FAISS vector store created successfully.")

if __name__ == "__main__":
    build_faiss()
