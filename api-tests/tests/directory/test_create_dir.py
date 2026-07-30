import uuid
import pytest


# 201
def test_create_directory(auth_api, directory_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, password)
    assert response.status_code == 200

    parent = f"parent_{uuid.uuid4().hex}"
    child = f"child_{uuid.uuid4().hex}"

    # Create parent directory
    response = directory_api.create_directory(
        path=parent
    )
    assert response.status_code == 201

    # Create child directory
    response = directory_api.create_directory(
        path=f"{parent}/{child}"
    )
    assert response.status_code == 201


# 400
@pytest.mark.parametrize(
    "path",
    [
        "",
        "./folder",
        "documents/\0/folder",
    ],
)
def test_create_directory_invalid_path(auth_api, directory_api, path):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, password)
    assert response.status_code == 200

    response = directory_api.create_directory(
        path=path
    )

    assert response.status_code == 400


# 401
def test_create_directory_unauthorized(directory_api):
    response = directory_api.create_directory(
        path="folder"
    )

    assert response.status_code == 401


# 404
def test_create_directory_parent_not_found(auth_api, directory_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, password)
    assert response.status_code == 200

    response = directory_api.create_directory(
        path=f"parent_{uuid.uuid4().hex}/child"
    )

    assert response.status_code == 404


# 409
def test_create_directory_already_exists(auth_api, directory_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, password)
    assert response.status_code == 200

    directory = f"folder_{uuid.uuid4().hex}"

    response = directory_api.create_directory(
        path=directory
    )
    assert response.status_code == 201

    response = directory_api.create_directory(
        path=directory
    )

    assert response.status_code == 409