import os
import sys
import json
import faiss
import numpy as np
from sentence_transformers import SentenceTransformer

# Fix project root path
sys.path.append(
    os.path.abspath(os.path.join(os.path.dirname(__file__), "../../"))
)

VECTOR_DIR = os.path.join(os.path.dirname(__file__), "faiss_index")
DOCS_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../knowledge/docs")
)

MODEL_NAME = "all-MiniLM-L6-v2"

def load_index():
    index = faiss.read_index(os.path.join(VECTOR_DIR, "index.faiss"))
    with open(os.path.join(VECTOR_DIR, "doc_ids.json"), "r") as f:
        ids = json.load(f)
    return index, ids

def load_docs():
    docs = {}
    for file in os.listdir(DOCS_DIR):
        if file.endswith(".json"):
            with open(os.path.join(DOCS_DIR, file), "r", encoding="utf-8") as f:
                doc = json.load(f)
                docs[doc["id"]] = doc["content"]
    return docs

def search(query, top_k=5):
    model = SentenceTransformer(MODEL_NAME)
    query_vec = model.encode([query]).astype("float32")

    index, ids = load_index()
    docs = load_docs()

    distances, indices = index.search(query_vec, top_k)

    print("\nTop matches:\n")
    for i in indices[0]:
        doc_id = ids[i]
        print("-----")
        print(docs.get(doc_id, "Not found"))

if __name__ == "__main__":
    user_query = input("Enter your question: ")
    search(user_query)
