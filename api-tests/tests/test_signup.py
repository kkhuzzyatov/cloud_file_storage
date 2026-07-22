import uuid
import pytest

def test_signup_success(api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = api.signup(username, password)

    assert response.status_code == 201

    body = response.json()

    assert "token" in body
    assert isinstance(body["token"], str)

    # Проверяем, что token является корректным UUID
    uuid.UUID(body["token"])


def test_signup_duplicate(api):
    username = f"user_{uuid.uuid4().hex}"

    api.signup(username, "password123")
    response = api.signup(username, "password123")

    assert response.status_code == 409


@pytest.mark.parametrize(
    "username,password",
    [
        ("", "password123"),
        ("12", "password123"),
        ("test_user_2", ""),
    ],
)
def test_signup_validation(api, username, password):
    response = api.signup(username, password)

    assert response.status_code == 400