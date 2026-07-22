import pytest

from client import ApiClient


@pytest.fixture
def api():
    return ApiClient()