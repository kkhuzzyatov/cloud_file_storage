import os

import requests
from dotenv import load_dotenv

load_dotenv()

RESOURCE_URL = os.getenv("RESOURCE_URL")

class ApiResourceClient:
    def __init__(self, session: requests.Session):
        self.session = session

    def list(self):
        return self.session.get(
            RESOURCE_URL,
            timeout=10,
        )

    def delete(self):
        return self.session.delete(
            RESOURCE_URL,
            timeout=10,
        )

    def download(self):
        return self.session.get(
            f"{RESOURCE_URL}/download",
            timeout=10,
        )

    def move(self):
        return self.session.post(
            f"{RESOURCE_URL}/move",
            timeout=10,
        )

    def search(self):
        return self.session.get(
            f"{RESOURCE_URL}/search",
            timeout=10,
        )

    def create(self):
        return self.session.post(
            RESOURCE_URL,
            timeout=10,
        )
        
    def upload(self, path: str, file_name: str, content: bytes):
        return self.session.post(
            RESOURCE_URL,
            params={"path": path},
            files={
                "file": (file_name, content, "text/plain"),
            }
        )
        
    def get(self, path: str):
        return self.session.get(
            RESOURCE_URL,
            params={"path": path}
        )
        
    def upload_without_body(self, path):
        return self.session.post(
            RESOURCE_URL,
            params={"path": path}
        )
        
    def delete(self, path: str):
        return self.session.delete(
            RESOURCE_URL,
            params={"path": path}
        )
        
    def download(self, path: str):
        return self.session.get(
            f"{RESOURCE_URL}/download",
            params={"path": path}
        )
        
    def move(self, from_path: str, to_path: str):
        return self.session.post(
            f"{RESOURCE_URL}/move",
            params={
                "from": from_path,
                "to": to_path,
            }
        )
    
    def search(self, query: str):
        return self.session.get(
            f"{RESOURCE_URL}/search",
            params={"query": query}
        )