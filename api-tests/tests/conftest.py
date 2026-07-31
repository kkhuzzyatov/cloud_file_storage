import uuid

import pytest
import requests

from auth_client import ApiAuthClient
from user_client import ApiUserClient
from resource_client import ApiResourceClient
from directory_client import ApiDirectoryClient

@pytest.fixture
def session():
    return requests.Session()

@pytest.fixture
def auth_api(session):
    return ApiAuthClient(session)

@pytest.fixture
def user_api(session):
    return ApiUserClient(session)

@pytest.fixture
def resource_api(session):
    return ApiResourceClient(session)

@pytest.fixture
def directory_api(session):
    return ApiDirectoryClient(session)


@pytest.fixture
def create_authenticated_clients():
    def factory():
        session = requests.Session()

        auth = ApiAuthClient(session)
        resource = ApiResourceClient(session)
        directory = ApiDirectoryClient(session)

        username = f"user_{uuid.uuid4().hex}"
        password = "password123"

        assert auth.signup(username, password).status_code == 201
        assert auth.signin(username, password).status_code == 200

        return auth, resource, directory

    return factory