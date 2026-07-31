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

    file_name = f"{uuid.uuid4().hex}.txt"
    content = uuid.uuid4().hex.encode("utf-8")
    folder_name = f"folder_{uuid.uuid4().hex}"

    # Upload file
    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content
    )
    assert response.status_code == 201

    # Delete resource
    response = resource_api.delete(
        path=f"{folder_name}/{file_name}"
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

    response = resource_api.delete(
        path=path
    )

    assert response.status_code == 400

# 401
def test_delete_resource_unauthorized(resource_api):
    response = resource_api.delete(
        path="folder/file.txt"
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

    response = resource_api.delete(
        path=f"folder_{uuid.uuid4().hex}/file.txt"
    )

    assert response.status_code == 404

def test_delete_other_user_resource(create_authenticated_clients):
    _, owner, _ = create_authenticated_clients()

    folder = f"folder_{uuid.uuid4().hex}"
    file = f"{uuid.uuid4().hex}.txt"

    owner.upload(folder, file, b"secret")

    _, attacker, _ = create_authenticated_clients()

    response = attacker.delete(f"{folder}/{file}")

    assert response.status_code == 404