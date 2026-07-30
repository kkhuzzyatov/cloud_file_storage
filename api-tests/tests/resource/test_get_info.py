import uuid
import pytest

# 200
def test_get_resource_info(auth_api, resource_api):
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

    # Generate folder
    folder_name = f"folder_{uuid.uuid4().hex}"

    # Upload file
    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content
    )
    assert response.status_code == 201

    # Get resource information
    response = resource_api.get(
        path=(folder_name + "/" + file_name)
    )

    assert response.status_code == 200

    body = response.json()

    assert body == {
        "path": (folder_name + "/" + file_name),
        "name": file_name,
        "size": len(content),
        "type": "FILE",
    }

# 400
@pytest.mark.parametrize(
    "path",
    [
        "./file.txt",
        "documents/\0/file.txt",
    ],
)
def test_upload_invalid_path(auth_api, resource_api, path):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    response = resource_api.upload(
        path=path,
        file_name=f"{uuid.uuid4().hex}.txt",
        content=b"test"
    )

    assert response.status_code == 400

# 401
def test_get_resource_info_unauthorized(resource_api):
    response = resource_api.get(
        path="folder/file.txt"
    )
    assert response.status_code == 401

# 404
def test_get_resource_info_not_found(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    # Request information for a non-existent resource
    response = resource_api.get(
        path=f"folder_{uuid.uuid4().hex}/file.txt"
    )

    assert response.status_code == 404