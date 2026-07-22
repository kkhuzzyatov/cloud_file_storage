import uuid
import pytest

def test_signup_success(auth_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)

    assert response.status_code == 201

    body = response.json()

    assert "token" in body
    assert isinstance(body["token"], str)

    # Проверяем, что token является корректным UUID
    uuid.UUID(body["token"])


def test_signup_duplicate(auth_api):
    username = f"user_{uuid.uuid4().hex}"

    auth_api.signup(username, "password123")
    response = auth_api.signup(username, "password123")

    assert response.status_code == 409


@pytest.mark.parametrize(
    "username,password",
    [
        ("", "password123"),
        ("12", "password123"),
        ("test_user_2", ""),
    ],
)
def test_signup_validation(auth_api, username, password):
    response = auth_api.signup(username, password)

    assert response.status_code == 400