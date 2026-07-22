import os

import requests
from dotenv import load_dotenv

load_dotenv()

AUTH_URL = os.getenv("AUTH_URL")


class ApiAuthClient:

    def signup(self, username: str, password: str):
        return requests.post(
            f"{AUTH_URL}/sign-up",
            json={
                "username": username,
                "password": password,
            },
            timeout=10,
        )

    def signin(self, username: str, password: str):
        return requests.post(
            f"{AUTH_URL}/sign-in",
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
            f"{AUTH_URL}/api/user/me",
            headers=headers,
            timeout=10,
        )