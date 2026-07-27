import uuid
import pytest


# 200
def test_search_resource(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    folder = f"folder_{uuid.uuid4().hex}"

    file1 = f"report_{uuid.uuid4().hex}.txt"
    file2 = f"notes_{uuid.uuid4().hex}.txt"

    content1 = b"report"
    content2 = b"notes"

    response = resource_api.upload(
        path=folder,
        file_name=file1,
        content=content1,
        token=token,
    )
    assert response.status_code == 201

    response = resource_api.upload(
        path=folder,
        file_name=file2,
        content=content2,
        token=token,
    )
    assert response.status_code == 201

    response = resource_api.search(
        query="report",
        token=token,
    )

    assert response.status_code == 200

    body = response.json()

    expected_path = f"{folder}/{file1}"

    assert any(resource["path"] == expected_path for resource in body)


# 400
@pytest.mark.parametrize(
    "query",
    [
        " "
        "text text"
    ],
)
def test_search_invalid_query(auth_api, resource_api, query):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    response = resource_api.search(
        query=query,
        token=token,
    )

    assert response.status_code == 400


# 401
def test_search_unauthorized(resource_api):
    response = resource_api.search(
        query="file",
        token="invalid_token",
    )

    assert response.status_code == 401