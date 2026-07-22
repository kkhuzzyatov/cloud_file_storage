import uuid

def test_me_unauthorized(api):
    response = api.me()

    assert response.status_code == 401

def test_me_success(api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Регистрация
    response = api.signup(username, password)
    assert response.status_code == 201

    # Вход
    response = api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    # Получение информации о пользователе
    response = api.me(token)

    assert response.status_code == 200

    body = response.json()

    # Проверяем структуру ответа
    assert set(body.keys()) == {"id", "username"}

    # Проверяем, что id является корректным UUID
    assert isinstance(body["id"], str)
    uuid.UUID(body["id"])

    # Проверяем username
    assert body["username"] == username