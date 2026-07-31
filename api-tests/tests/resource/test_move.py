import uuid
import pytest

# 200
def test_move_resource(auth_api, resource_api):
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

    from_folder = f"from_{uuid.uuid4().hex}"
    to_folder = f"to_{uuid.uuid4().hex}"

    from_path = f"{from_folder}/{file_name}"
    to_path = f"{to_folder}/{file_name}"

    # Create resource
    response = resource_api.upload(
        path=from_folder,
        file_name=file_name,
        content=content
    )
    assert response.status_code == 201

    # Move resource
    response = resource_api.move(
        from_path=from_path,
        to_path=to_folder
    )
    assert response.status_code == 200

    # Check resource exists in destination
    response = resource_api.get(
        path=to_path
    )

    assert response.status_code == 200

    body = response.json()

    assert body == {
        "path": to_path,
        "name": file_name,
        "size": len(content),
        "type": "FILE",
    }


# 400
@pytest.mark.parametrize(
    "from_path,to_path",
    [
        ("./file.txt", "folder/file.txt"),
        ("folder/file.txt", "./file.txt"),
        ("documents/\0/file.txt", "folder/file.txt"),
        ("folder/file.txt", "documents/\0/file.txt"),
    ],
)
def test_move_resource_invalid_path(
    auth_api,
    resource_api,
    from_path,
    to_path,
):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    response = resource_api.move(
        from_path=from_path,
        to_path=to_path
    )

    assert response.status_code == 400


# 401
def test_move_resource_unauthorized(resource_api):
    response = resource_api.move(
        from_path="folder/file.txt",
        to_path="folder2"
    )

    assert response.status_code == 401


# 404
def test_move_resource_not_found(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    response = resource_api.move(
        from_path=f"folder_{uuid.uuid4().hex}/file.txt",
        to_path=f"folder2_{uuid.uuid4().hex}"
    )

    assert response.status_code == 404

def test_move_other_user_resource(create_authenticated_clients):
    _, owner, _ = create_authenticated_clients()

    folder = f"folder_{uuid.uuid4().hex}"
    file = f"{uuid.uuid4().hex}.txt"

    owner.upload(folder, file, b"secret")

    _, attacker, _ = create_authenticated_clients()

    response = attacker.move(
        from_path=f"{folder}/{file}",
        to_path="another_folder",
    )

    assert response.status_code == 404

# 409
def test_move_resource_conflict(auth_api, resource_api):
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

    from_folder = f"from_{uuid.uuid4().hex}"
    to_folder = f"to_{uuid.uuid4().hex}"

    from_path = f"{from_folder}/{file_name}"
    to_path = f"{to_folder}/{file_name}"

    # Create resource
    response = resource_api.upload(
        path=from_folder,
        file_name=file_name,
        content=content
    )
    assert response.status_code == 201

    # Move resource
    response = resource_api.move(
        from_path=from_path,
        to_path=to_folder
    )
    assert response.status_code == 200

    # Check resource exists in destination
    response = resource_api.get(
        path=to_path
    )

    assert response.status_code == 200

    # Create resource
    response = resource_api.upload(
        path=from_folder,
        file_name=file_name,
        content=content
    )
    assert response.status_code == 201

    # Move resource
    response = resource_api.move(
        from_path=from_path,
        to_path=to_folder
    )
    assert response.status_code == 409