import pytest

from auth_client import ApiAuthClient
from user_client import ApiUserClient
from resource_client import ApiResourceClient

@pytest.fixture
def auth_api():
    return ApiAuthClient()

@pytest.fixture
def user_api():
    return ApiUserClient()

@pytest.fixture
def resource_api():
    return ApiResourceClient()