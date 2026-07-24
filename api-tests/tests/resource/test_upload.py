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

    token = response.json()["token"]

    # Generate random file
    file_name = f"{uuid.uuid4().hex}.txt"
    content = uuid.uuid4().hex.encode("utf-8")

    # Generate random folder
    folder_name = f"folder_{uuid.uuid4().hex}"

    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content,
        token=token,
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

    token = response.json()["token"]

    # Send request with auth token but without file body
    response = resource_api.upload_without_body(
        path=f"folder_{uuid.uuid4().hex}",
        token=token,
    )

    assert response.status_code == 400

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

    token = response.json()["token"]

    response = resource_api.upload(
        path=path,
        file_name=f"{uuid.uuid4().hex}.txt",
        content=b"test",
        token=token,
    )

    assert response.status_code == 400

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

    token = response.json()["token"]

    # Генерируем файл
    file_name = f"{uuid.uuid4().hex}.txt"
    content = uuid.uuid4().hex.encode("utf-8")

    # Генерируем папку
    folder_name = f"folder_{uuid.uuid4().hex}"

    # Первая загрузка
    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content,
        token=token,
    )

    assert response.status_code == 201

    # Повторная загрузка того же файла
    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content,
        token=token,
    )

    assert response.status_code == 409
