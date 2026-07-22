import os

import requests
from dotenv import load_dotenv

load_dotenv()

BASE_URL = os.getenv("BASE_URL")


class ApiClient:

    def signup(self, username: str, password: str):
        return requests.post(
            f"{BASE_URL}/api/auth/sign-up",
            json={
                "username": username,
                "password": password,
            },
            timeout=10,
        )

    def signin(self, username: str, password: str):
        return requests.post(
            f"{BASE_URL}/api/auth/sign-in",
            json={
                "username": username,
                "password": password,
            },
            timeout=10,
        )
        
    def me(self, token: str | None = None):
        headers = {}

        if token is not None:
            headers["Authorization"] = f"Bearer {token}"

        return requests.get(
            f"{BASE_URL}/api/user/me",
            headers=headers,
            timeout=10,
        )