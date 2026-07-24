import os
import io

import requests
from dotenv import load_dotenv

load_dotenv()

RESOURCE_URL = os.getenv("RESOURCE_URL")


class ApiResourceClient:

    def list(self, token: str | None = None):
        return requests.get(
            RESOURCE_URL,
            headers=self._headers(token),
            timeout=10,
        )

    def delete(self, token: str | None = None):
        return requests.delete(
            RESOURCE_URL,
            headers=self._headers(token),
            timeout=10,
        )

    def download(self, token: str | None = None):
        return requests.get(
            f"{RESOURCE_URL}/download",
            headers=self._headers(token),
            timeout=10,
        )

    def move(self, token: str | None = None):
        return requests.post(
            f"{RESOURCE_URL}/move",
            headers=self._headers(token),
            timeout=10,
        )

    def search(self, token: str | None = None):
        return requests.get(
            f"{RESOURCE_URL}/search",
            headers=self._headers(token),
            timeout=10,
        )

    def create(self, token: str | None = None):
        return requests.post(
            RESOURCE_URL,
            headers=self._headers(token),
            timeout=10,
        )
        
    def upload(self, path: str, file_name: str, content: bytes, token: str):
        return requests.post(
            RESOURCE_URL,
            params={"path": path},
            headers={
                "Authorization": f"Bearer {token}",
            },
            files={
                "file": (file_name, content, "text/plain"),
            },
            timeout=10,
        )
        
    def get(self, path: str, token: str):
        return requests.get(
            RESOURCE_URL,
            params={"path": path},
            headers={
                "Authorization": f"Bearer {token}",
            },
            timeout=10,
        )
        
    def upload_without_body(self, path, token):
        return requests.post(
            RESOURCE_URL,
            params={"path": path},
            headers={"Authorization": f"Bearer {token}"},
            timeout=10,
        )
        
    def delete(self, path: str, token: str):
        return requests.delete(
            RESOURCE_URL,
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