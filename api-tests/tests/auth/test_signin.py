import uuid

# 200
def test_signin_success(auth_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Регистрация
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Вход
    response = auth_api.signin(username, password)

    assert response.status_code == 200

# 400
def test_signin_wrong_password(auth_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, "wrong_password")

    assert response.status_code == 400
    
def test_signin_unknown_user(auth_api):
    username = f"user_{uuid.uuid4().hex}"

    response = auth_api.signin(username, "password123")

    assert response.status_code == 400