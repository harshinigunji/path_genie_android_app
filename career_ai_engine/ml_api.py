from flask import Flask, request, jsonify
import joblib
import pandas as pd

app = Flask(__name__)

# Load trained ML model
model = joblib.load("career_recommendation_model.pkl")

# Feature names (MUST match training data exactly)
FEATURE_NAMES = [
    "education_level",
    "interest_tech",
    "interest_commerce",
    "interest_arts",
    "career_preference_private",
    "difficulty_tolerance",
    "risk_tolerance",
    "option_type",
    "option_difficulty",
    "option_job_type"
]

@app.route("/predict", methods=["POST"])
def predict():
    try:
        data = request.json

        # Build feature list in correct order
        features = [
            data["education_level"],
            data["interest_tech"],
            data["interest_commerce"],
            data["interest_arts"],
            data["career_preference_private"],
            data["difficulty_tolerance"],
            data["risk_tolerance"],
            data["option_type"],
            data["option_difficulty"],
            data["option_job_type"]
        ]

        # Convert to DataFrame to match training schema
        input_df = pd.DataFrame([features], columns=FEATURE_NAMES)

        # Predict score
        score = model.predict(input_df)[0]

        return jsonify({
            "status": True,
            "recommendation_score": int(round(score))
        })

    except Exception as e:
        return jsonify({
            "status": False,
            "error": str(e)
        }), 400


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
