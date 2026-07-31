import uuid
import pytest


# 200
def test_search_resource(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    folder = f"folder_{uuid.uuid4().hex}"

    file1 = f"report_{uuid.uuid4().hex}.txt"
    file2 = f"notes_{uuid.uuid4().hex}.txt"

    content1 = b"report"
    content2 = b"notes"

    response = resource_api.upload(
        path=folder,
        file_name=file1,
        content=content1
    )
    assert response.status_code == 201

    response = resource_api.upload(
        path=folder,
        file_name=file2,
        content=content2,
    )
    assert response.status_code == 201

    response = resource_api.search(
        query="report",
    )

    assert response.status_code == 200

    body = response.json()

    expected_path = f"{folder}/{file1}"

    assert any(resource["path"] == expected_path for resource in body)

def test_search_does_not_return_other_user_resources(create_authenticated_clients):
    _, owner, _ = create_authenticated_clients()

    folder = f"folder_{uuid.uuid4().hex}"
    file = f"report_{uuid.uuid4().hex}.txt"

    owner.upload(folder, file, b"secret")

    _, attacker, _ = create_authenticated_clients()

    response = attacker.search("report")

    assert response.status_code == 200

    body = response.json()

    assert all(resource["name"] != file for resource in body)

# 400
@pytest.mark.parametrize(
    "query",
    [
        " "
        "text text"
    ],
)
def test_search_invalid_query(auth_api, resource_api, query):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    response = auth_api.signup(username, password)
    assert response.status_code == 201

    response = auth_api.signin(username, password)
    assert response.status_code == 200

    response = resource_api.search(
        query=query,
    )

    assert response.status_code == 400


# 401
def test_search_unauthorized(resource_api):
    response = resource_api.search(
        query="file"
    )

    assert response.status_code == 401