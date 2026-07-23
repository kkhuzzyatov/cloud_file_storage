import uuid


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