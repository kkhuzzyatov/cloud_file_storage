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