"""Start the opt-in local marketplace demo, seeded with 12 fictional campus listings."""
from pathlib import Path
import os
import subprocess

if __name__ == "__main__":
    backend = Path(__file__).resolve().parents[1] / "backend"
    command = [str(backend / "mvnw.cmd")] if os.name == "nt" else ["bash", "./mvnw"]
    raise SystemExit(subprocess.call(command + ["spring-boot:run", "-Dspring-boot.run.profiles=demo"], cwd=backend))
