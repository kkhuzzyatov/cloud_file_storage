import os

import requests
from dotenv import load_dotenv

load_dotenv()

USER_URL = os.getenv("USER_URL")


class ApiUserClient:
    def __init__(self, session: requests.Session):
        self.session = session

    def me(self):
        return self.session.get(
            f"{USER_URL}/me",
            timeout=10,
        )