import subprocess
import sys

print("Testing Ollama from Python...")
try:
    result = subprocess.run(
        ["ollama", "run", "mistral", "hello"],
        text=True,
        encoding="utf-8",
        errors="ignore",
        capture_output=True
    )
    print(f"Return Code: {result.returncode}")
    print(f"Stdout: {result.stdout.strip()}")
    print(f"Stderr: {result.stderr.strip()}")
except Exception as e:
    print(f"Exception: {e}")
