import os

import requests
from dotenv import load_dotenv

load_dotenv()

USER_URL = os.getenv("USER_URL")


class ApiUserClient:

    def me(self, token: str | None = None):
        headers = {}

        if token is not None:
            headers["Authorization"] = f"Bearer {token}"

        return requests.get(
            f"{USER_URL}/me",
            headers=headers,
            timeout=10,
        )