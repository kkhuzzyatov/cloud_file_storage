import os

import requests
from dotenv import load_dotenv

load_dotenv()

DIRECTORY_URL = os.getenv("DIRECTORY_URL")


class ApiDirectoryClient:

    def directory(self, path: str, token: str):
        return requests.get(
            DIRECTORY_URL,
            params={"path": path},
            headers={
                "Authorization": f"Bearer {token}",
            },
            timeout=10,
        )
        
    def create_directory(self, path: str, token: str):
        return requests.post(
            DIRECTORY_URL,
            params={"path": path},
            headers={
                "Authorization": f"Bearer {token}",
            },
            timeout=10,
        )

    @staticmethod
    def _headers(token: str | None):
        if token:
            return {"Authorization": f"Bearer {token}"}
        return {}