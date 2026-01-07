import sys
import os
import mysql.connector
import json

# -------------------------------
# Project root
# -------------------------------
sys.path.append(
    os.path.abspath(os.path.join(os.path.dirname(__file__), "../../"))
)

from data.db_config import DB_CONFIG

# -------------------------------
# Output directory
# -------------------------------
OUTPUT_DIR = os.path.join(os.path.dirname(__file__), "docs")

# -------------------------------
# Hard exclusions (NON-NEGOTIABLE)
# -------------------------------
EXCLUDED_KEYWORDS = [
    "user",
    "roadmap",
    "saved",
    "old",
    "backup",
    "history"
]

# -------------------------------
# Utilities
# -------------------------------
def ensure_output_dir():
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    for file in os.listdir(OUTPUT_DIR):
        if file.endswith(".json"):
            os.remove(os.path.join(OUTPUT_DIR, file))

def get_tables(cursor):
    cursor.execute("SHOW TABLES")
    return [t[0] for t in cursor.fetchall()]

def is_allowed_table(table_name: str) -> bool:
    name = table_name.lower()
    for word in EXCLUDED_KEYWORDS:
        if word in name:
            return False
    return True

def read_table(cursor, table_name):
    cursor.execute(f"SELECT * FROM `{table_name}`")
    columns = [col[0] for col in cursor.description]
    rows = cursor.fetchall()
    return columns, rows

# -------------------------------
# Load lookup dictionaries
# -------------------------------
def load_lookups(cursor):
    lookups = {}

    def load(table, id_col, name_col):
        try:
            cursor.execute(f"SELECT {id_col}, {name_col} FROM `{table}`")
            return {row[0]: row[1] for row in cursor.fetchall()}
        except:
            return {}

    lookups["education_levels"] = load("education_levels", "education_level_id", "level_name")
    lookups["streams"] = load("streams", "stream_id", "stream_name")
    lookups["jobs"] = load("jobs", "job_id", "job_name")
    lookups["entrance_exams"] = load("entrance_exams", "exam_id", "exam_name")

    return lookups

# -------------------------------
# Knowledge generation logic
# -------------------------------
def generate_fact(table, columns, row, lookups):

    def val(col):
        return row[columns.index(col)]

    # -------------------------------
    # Stream progression
    # -------------------------------
    if table == "stream_progression":
        from_name = lookups["streams"].get(val("current_stream_id"))
        to_name = lookups["streams"].get(val("next_stream_id"))
        if from_name and to_name:
            return f"After completing {from_name}, students can choose {to_name}."

    # -------------------------------
    # Stream → Jobs
    # -------------------------------
    if table == "stream_jobs":
        stream = lookups["streams"].get(val("stream_id"))
        job = lookups["jobs"].get(val("job_id"))
        if stream and job:
            return f"For {stream}, jobs available are {job}."

    # -------------------------------
    # Stream → Exams
    # -------------------------------
    if table == "stream_exams":
        stream = lookups["streams"].get(val("stream_id"))
        exam = lookups["entrance_exams"].get(val("exam_id"))
        if stream and exam:
            return f"For {stream}, exams required are {exam}."

    # -------------------------------
    # Education level → Jobs
    # -------------------------------
    if table == "education_level_jobs":
        level = lookups["education_levels"].get(val("education_level_id"))
        job = lookups["jobs"].get(val("job_id"))
        if level and job:
            return f"For {level}, jobs available are {job}."

    # -------------------------------
    # Education level → Exams
    # -------------------------------
    if table == "education_level_exams":
        level = lookups["education_levels"].get(val("education_level_id"))
        exam = lookups["entrance_exams"].get(val("exam_id"))
        if level and exam:
            return f"For {level}, exams required are {exam}."

    # -------------------------------
    # Streams table
    # -------------------------------
    if table == "streams":
        level_id = val("education_level_id")
        stream_name = val("stream_name")
        level_name = lookups["education_levels"].get(level_id)
        if level_name:
            return f"After completing {level_name}, students can choose {stream_name}."

    # -------------------------------
    # Fallback: ignore noisy tables
    # -------------------------------
    return ""

# -------------------------------
# Main generator
# -------------------------------
def generate():
    ensure_output_dir()

    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    lookups = load_lookups(cursor)
    tables = get_tables(cursor)

    doc_count = 0
    skipped = []

    for table in tables:
        if not is_allowed_table(table):
            skipped.append(table)
            continue

        columns, rows = read_table(cursor, table)

        for idx, row in enumerate(rows):
            content = generate_fact(table, columns, row, lookups)
            if not content:
                continue

            doc = {
                "id": f"{table}_{idx}",
                "content": content
            }

            with open(
                os.path.join(OUTPUT_DIR, f"{table}_{idx}.json"),
                "w",
                encoding="utf-8"
            ) as f:
                json.dump(doc, f, indent=2, ensure_ascii=False)

            doc_count += 1

    cursor.close()
    conn.close()

    print("Knowledge generation completed.")
    print("Documents created:", doc_count)
    print("Skipped tables:", skipped)

# -------------------------------
if __name__ == "__main__":
    generate()
