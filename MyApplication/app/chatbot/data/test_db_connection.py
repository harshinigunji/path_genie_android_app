import mysql.connector
from db_config import DB_CONFIG

def test_connection():
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor()

    cursor.execute("SHOW TABLES")
    tables = cursor.fetchall()

    print("Connected successfully!")
    print("Tables in database:")

    for t in tables:
        print("-", t[0])

    cursor.close()
    conn.close()

if __name__ == "__main__":
    test_connection()
