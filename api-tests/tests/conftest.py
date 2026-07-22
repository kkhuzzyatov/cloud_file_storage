import pytest

from auth_client import ApiAuthClient
from user_client import ApiUserClient

@pytest.fixture
def auth_api():
    return ApiAuthClient()

@pytest.fixture
def user_api():
    return ApiUserClient()