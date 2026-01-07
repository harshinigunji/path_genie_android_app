import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
import joblib

# Load dataset
data = pd.read_csv("career_recommendation_data.csv")

# Split features and target
X = data.drop("recommendation_score", axis=1)
y = data["recommendation_score"]

# Train-test split
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42
)

# Create model
model = RandomForestRegressor(
    n_estimators=100,
    random_state=42
)

# Train model
model.fit(X_train, y_train)

# Evaluate model
accuracy = model.score(X_test, y_test)
print("Model accuracy (R² score):", accuracy)

# Save trained model
joblib.dump(model, "career_recommendation_model.pkl")

print("Model trained and saved successfully")
