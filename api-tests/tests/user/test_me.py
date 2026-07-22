import uuid

def test_me_unauthorized(user_api):
    response = user_api.me()
    assert response.status_code == 401

def test_me_success(auth_api, user_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Регистрация
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Вход
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    # Получение информации о пользователе
    response = user_api.me(token)

    assert response.status_code == 200

    body = response.json()

    # Проверяем структуру ответа
    assert set(body.keys()) == {"id", "username"}

    # Проверяем, что id является корректным UUID
    assert isinstance(body["id"], str)
    uuid.UUID(body["id"])

    # Проверяем username
    assert body["username"] == username