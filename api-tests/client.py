import os

import requests
from dotenv import load_dotenv

load_dotenv()

BASE_URL = os.getenv("BASE_URL")

if BASE_URL is None:
    raise RuntimeError("BASE_URL is not configured")


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