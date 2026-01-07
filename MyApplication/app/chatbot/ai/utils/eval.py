import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "../../")))

from ai.models.rag_chat_test import retrieve_context, ask_ollama

TEST_QUESTIONS = [
    "What streams are available after 12th Science?",
    "What exams are required after degree?",
    "What jobs can I get after Engineering?",
    "Streams after 12th Science",
    "Exams after degree",
    "Jobs after specific streams",
    "What are the career options after completing Science (PCM)?",
    "Which exams do I need for Engineering?",
    "Jobs available for Computer Science stream"
]

def evaluate():
    for query in TEST_QUESTIONS:
        print(f"Query: {query}")
        context = retrieve_context(query)
        print(f"Retrieved Context:\n{context}\n")
        
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
        print(f"Answer: {answer}\n{'-'*50}\n")

if __name__ == "__main__":
    evaluate()