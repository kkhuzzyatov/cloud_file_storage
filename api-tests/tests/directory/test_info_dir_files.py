import uuid
import pytest


# 200
def test_get_directory_content(auth_api, resource_api, directory_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    folder = f"folder_{uuid.uuid4().hex}"
    file_name = f"{uuid.uuid4().hex}.txt"
    content = uuid.uuid4().hex.encode("utf-8")

    response = resource_api.upload(
        path=folder,
        file_name=file_name,
        content=content,
        token=token,
    )
    assert response.status_code == 201

    response = directory_api.directory(
        path=folder,
        token=token,
    )

    assert response.status_code == 200

    body = response.json()

    assert isinstance(body, list)
    assert len(body) == 1

    resource = body[0]

    assert resource["name"] == file_name
    assert resource["size"] == len(content)
    assert resource["type"] == "FILE"

    assert resource["path"].startswith(folder)
    assert resource["path"].endswith(file_name)


# 400
@pytest.mark.parametrize(
    "path",
    [
        "",
        "./folder",
        "documents/\0/folder",
    ],
)
def test_get_directory_invalid_path(auth_api, directory_api, path):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    response = directory_api.directory(
        path=path,
        token=token,
    )

    assert response.status_code == 400


# 401
def test_get_directory_unauthorized(directory_api):
    response = directory_api.directory(
        path="folder",
        token="invalid_token",
    )

    assert response.status_code == 401


# 404
def test_get_directory_not_found(auth_api, directory_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    response = directory_api.directory(
        path=f"folder_{uuid.uuid4().hex}",
        token=token,
    )

    assert response.status_code == 404