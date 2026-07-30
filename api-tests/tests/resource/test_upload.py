import uuid
import pytest

# 201
def test_upload_file(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    # Generate random file
    file_name = f"{uuid.uuid4().hex}.txt"
    content = uuid.uuid4().hex.encode("utf-8")

    # Generate random folder
    folder_name = f"folder_{uuid.uuid4().hex}"

    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content
    )

    assert response.status_code == 201

    body = response.json()

    assert isinstance(body, list)
    assert len(body) == 1

    resource = body[0]

    assert resource == {
        "path": folder_name,
        "name": file_name,
        "size": len(content),
        "type": "FILE",
    }

# 400
def test_upload_without_body(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    # Send request
    response = resource_api.upload_without_body(
        path=f"folder_{uuid.uuid4().hex}"
    )

    assert response.status_code == 400

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
def test_upload_unauthorized(resource_api):
    response = resource_api.upload(
        path="folder",
        file_name="file.txt",
        content=b"test"
    )
    assert response.status_code == 401

# 409
def test_upload_duplicate_file(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Регистрация
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Авторизация
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    # Генерируем файл
    file_name = f"{uuid.uuid4().hex}.txt"
    content = uuid.uuid4().hex.encode("utf-8")

    # Генерируем папку
    folder_name = f"folder_{uuid.uuid4().hex}"

    # Первая загрузка
    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content
    )

    assert response.status_code == 201

    # Повторная загрузка того же файла
    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content
    )

    assert response.status_code == 409
