import uuid
import pytest
import base64

# 200
def test_download_resource(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    # Generate file
    file_name = f"{uuid.uuid4().hex}.txt"
    content = uuid.uuid4().hex.encode("utf-8")
    folder_name = f"folder_{uuid.uuid4().hex}"

    path = f"{folder_name}/{file_name}"

    # Upload file
    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content
    )

    assert response.status_code == 201

    # Download file
    response = resource_api.download(
        path=path
    )

    assert response.status_code == 200

    assert response.content == content


# 400
@pytest.mark.parametrize(
    "path",
    [
        "./file.txt",
        "documents/\0/file.txt",
    ],
)
def test_download_invalid_path(auth_api, resource_api, path):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    response = resource_api.download(
        path=path
    )

    assert response.status_code == 400


# 401
def test_download_resource_unauthorized(resource_api):
    response = resource_api.download(
        path="folder/file.txt"
    )

    assert response.status_code == 401


# 404
def test_download_resource_not_found(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    response = resource_api.download(
        path=f"folder_{uuid.uuid4().hex}/file.txt"
    )

    assert response.status_code == 404

def test_download_other_user_resource(create_authenticated_clients):
    _, owner, _ = create_authenticated_clients()

    folder = f"folder_{uuid.uuid4().hex}"
    file = f"{uuid.uuid4().hex}.txt"

    owner.upload(folder, file, b"secret")

    _, attacker, _ = create_authenticated_clients()

    response = attacker.download(f"{folder}/{file}")

    assert response.status_code == 404