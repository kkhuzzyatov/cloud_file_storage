import os

import requests
from dotenv import load_dotenv

load_dotenv()

DIRECTORY_URL = os.getenv("DIRECTORY_URL")


class ApiDirectoryClient:
    def __init__(self, session: requests.Session):
        self.session = session

    def directory(self, path: str):
        return self.session.get(
            DIRECTORY_URL,
            params={"path": path},
            timeout=10,
        )
        
    def create_directory(self, path: str):
        return self.session.post(
            DIRECTORY_URL,
            params={"path": path},
            timeout=10,
        )