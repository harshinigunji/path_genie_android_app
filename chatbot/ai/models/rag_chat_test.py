import os
import sys
import json
import faiss
import numpy as np
import subprocess
from sentence_transformers import SentenceTransformer

# Fix project root path
sys.path.append(
    os.path.abspath(os.path.join(os.path.dirname(__file__), "../../"))
)

# Paths
VECTOR_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../vector_store/faiss_index")
)

DOCS_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../knowledge/docs")
)

MODEL_NAME = "all-MiniLM-L6-v2"
OLLAMA_MODEL = "mistral"

def load_faiss():
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

def retrieve_context(query, top_k=5):
    model = SentenceTransformer(MODEL_NAME)
    query_vec = model.encode([query]).astype("float32")

    index, ids = load_faiss()
    docs = load_docs()

    distances, indices = index.search(query_vec, top_k)

    retrieved_ids = [ids[i] for i in indices[0]]
    print(f"DEBUG: Retrieved doc IDs for '{query}': {retrieved_ids}")

    context_chunks = []
    for i in indices[0]:
        doc_id = ids[i]
        context_chunks.append(docs.get(doc_id, ""))

    return "\n\n".join(context_chunks)

def ask_ollama(prompt):
    result = subprocess.run(
        ["ollama", "run", OLLAMA_MODEL],
        input=prompt,
        text=True,
        encoding="utf-8",
        errors="ignore",
        capture_output=True
    )
    return result.stdout.strip()


def chat():
    print("Local Education Stream Advisor Chatbot")
    print("Type 'exit' to quit\n")

    while True:
        query = input("You: ")
        if query.lower() == "exit":
            break

        context = retrieve_context(query)

        prompt = f"""
You are an education and career advisor chatbot.

RULES:
- Answer ONLY using the context below
- Do NOT use outside knowledge
- Keep the answer short and clear
- If the answer is not found, say:
  "Data not available in the system."

CONTEXT:
{context}

QUESTION:
{query}

ANSWER:
"""

        answer = ask_ollama(prompt)
        print("\nBot:", answer, "\n")

if __name__ == "__main__":
    chat()
