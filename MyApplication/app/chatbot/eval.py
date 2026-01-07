from ai.models.rag_core import retrieve_context, ask_ollama

TEST_QUESTIONS = [
    "streams",
    "jobs",
    "exams",
    "streams after 12th Science",
    "jobs after BCA",
    "exams required for BCA"
]

GENERIC_QUERIES = {"streams", "jobs", "exams"}

def is_too_generic(query: str) -> bool:
    return query.strip().lower() in GENERIC_QUERIES

def evaluate():
    for query in TEST_QUESTIONS:
        print(f"\nQuery: {query}")

        if is_too_generic(query):
            print("Answer: Please specify the education level or stream clearly.")
            print("-" * 60)
            continue

        context = retrieve_context(query)
        print(f"Retrieved Context:\n{context}\n")

        prompt = f"""
You are an education and career advisor chatbot.

RULES:
- Use ONLY facts from the context
- Do NOT infer or explain
- ONE short sentence only

DECISION:
- If unclear, reply exactly:
  Please specify the details clearly.

CONTEXT:
{context}

QUESTION:
{query}

ANSWER:
"""

        answer = ask_ollama(prompt).strip()

        if "please specify" in answer.lower() or "data not available" in answer.lower():
            answer = "Please specify the details clearly."

        print(f"Answer: {answer}")
        print("-" * 60)

if __name__ == "__main__":
    evaluate()
