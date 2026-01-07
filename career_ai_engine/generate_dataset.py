import csv
import random

output_file = "career_recommendation_data.csv"

header = [
    "education_level",
    "interest_tech",
    "interest_commerce",
    "interest_arts",
    "career_preference_private",
    "difficulty_tolerance",
    "risk_tolerance",
    "option_type",
    "option_difficulty",
    "option_job_type",
    "recommendation_score"
]

rows = []

def generate_row():
    education_level = random.randint(1,7)

    interest = random.choice(["tech", "commerce", "arts"])
    interest_tech = 1 if interest == "tech" else 0
    interest_commerce = 1 if interest == "commerce" else 0
    interest_arts = 1 if interest == "arts" else 0

    career_preference_private = random.choice([0,1])
    difficulty_tolerance = random.randint(1,3)
    risk_tolerance = random.randint(1,3)

    option_type = random.randint(1,3)
    option_difficulty = random.randint(1,3)

    if option_type == 3:
        option_job_type = career_preference_private
    else:
        option_job_type = 0

    base_score = 50

    if interest_tech == 1 and option_type == 1:
        base_score += 20
    if interest_commerce == 1 and option_type == 3:
        base_score += 20
    if interest_arts == 1 and option_type == 1:
        base_score += 15

    if difficulty_tolerance >= option_difficulty:
        base_score += 10
    else:
        base_score -= 10

    if risk_tolerance >= 2:
        base_score += 5

    recommendation_score = min(
        max(base_score + random.randint(-5,5), 30),
        95
    )

    return [
        education_level,
        interest_tech,
        interest_commerce,
        interest_arts,
        career_preference_private,
        difficulty_tolerance,
        risk_tolerance,
        option_type,
        option_difficulty,
        option_job_type,
        recommendation_score
    ]

for _ in range(500):
    rows.append(generate_row())

with open(output_file, "w", newline="") as f:
    writer = csv.writer(f)
    writer.writerow(header)
    writer.writerows(rows)

print("Dataset generated successfully with 500 rows")
