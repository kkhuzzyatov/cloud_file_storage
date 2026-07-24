import uuid
import pytest

# 204
def test_delete_resource(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    file_name = f"{uuid.uuid4().hex}.txt"
    content = uuid.uuid4().hex.encode("utf-8")
    folder_name = f"folder_{uuid.uuid4().hex}"

    # Upload file
    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content,
        token=token,
    )
    assert response.status_code == 201

    # Delete resource
    response = resource_api.delete(
        path=f"{folder_name}/{file_name}",
        token=token,
    )

    assert response.status_code == 204

# 400
@pytest.mark.parametrize(
    "path",
    [
        "./file.txt",
        "documents/\0/file.txt",
    ],
)
def test_delete_resource_invalid_path(auth_api, resource_api, path):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    response = resource_api.delete(
        path=path,
        token=token,
    )

    assert response.status_code == 400

# 401
def test_delete_resource_unauthorized(resource_api):
    response = resource_api.delete(
        path="folder/file.txt",
        token="invalid_token",
    )
    assert response.status_code == 401

# 404
def test_delete_resource_not_found(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    response = resource_api.delete(
        path=f"folder_{uuid.uuid4().hex}/file.txt",
        token=token,
    )

    assert response.status_code == 404