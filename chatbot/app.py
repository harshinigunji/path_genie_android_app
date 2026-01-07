from flask import Flask, request, jsonify
from ai.models.rag_core import retrieve_context, ask_ollama

app = Flask(__name__)

# -------------------------
# Generic query guard
# -------------------------
GENERIC_QUERIES = {"streams", "jobs", "exams"}

def is_too_generic(query: str) -> bool:
    return query.strip().lower() in GENERIC_QUERIES

# -------------------------
# Chat endpoint
# -------------------------
@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json()
    user_query = data.get("message", "").strip()

    if not user_query:
        return jsonify({
            "status": True,
            "reply": "Please specify the details clearly."
        })

    # Generic / incomplete question
    if is_too_generic(user_query):
        return jsonify({
            "status": True,
            "reply": "Please specify the education level or stream clearly."
        })

    context = retrieve_context(user_query)

    prompt = f"""Context:
{context}

Question:
{user_query}

Answer using Context only. Be short.
Answer:"""

    answer = ask_ollama(prompt).strip()
    
    # Cleaning for tinyllama hallucinations
    if "Answer:" in answer:
        answer = answer.split("Answer:")[-1].strip()
    if "Context:" in answer:
        answer = answer.split("Context:")[0].strip()
    
    # Remove "Sure!" prefixes
    if answer.lower().startswith("sure!") or answer.lower().startswith("here is"):
         answer = answer.split("\n", 1)[-1].strip()

    # Fallback for empty or error answers
    if not answer or answer.startswith("Error:"):
        # Log the error internally if needed
        print(f"AI Generation Failed: {answer}")
        
        # Friendly error for the user
        if "installed?" in answer:
             answer = "I'm having trouble thinking. Please ask the admin to install the 'mistral' AI model."
        else:
             answer = "I'm having a technical issue processing that request. Please try again."

    # 🔒 HARD ENFORCEMENT
    if "please specify" in answer.lower() or "data not available" in answer.lower():
        answer = "Please specify the details clearly."

    return jsonify({
        "status": True,
        "reply": answer
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
