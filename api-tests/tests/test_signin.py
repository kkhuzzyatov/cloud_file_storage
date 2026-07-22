import uuid

def test_signin_success(api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Регистрация
    response = api.signup(username, password)
    assert response.status_code == 201

    # Вход
    response = api.signin(username, password)

    assert response.status_code == 200

    body = response.json()

    assert "token" in body
    assert isinstance(body["token"], str)

    # UUID-токен
    uuid.UUID(body["token"])
    
def test_signin_wrong_password(api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = api.signup(username, password)
    assert response.status_code == 201

    response = api.signin(username, "wrong_password")

    assert response.status_code == 400
    
def test_signin_unknown_user(api):
    username = f"user_{uuid.uuid4().hex}"

    response = api.signin(username, "password123")

    assert response.status_code == 400