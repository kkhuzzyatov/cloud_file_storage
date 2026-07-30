import os
import requests
from dotenv import load_dotenv

load_dotenv()

AUTH_URL = os.getenv("AUTH_URL")


class ApiAuthClient:

    def __init__(self, session: requests.Session):
        self.session = session

    def signup(self, username, password):
        return self.session.post(
            f"{AUTH_URL}/sign-up",
            json={
                "username": username,
                "password": password,
            },
            timeout=10,
        )

    def signin(self, username, password):
        return self.session.post(
            f"{AUTH_URL}/sign-in",
            json={
                "username": username,
                "password": password,
            },
            timeout=10,
        )