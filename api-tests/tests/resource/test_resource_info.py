import uuid

def test_get_resource_info(auth_api, resource_api):
    username = f"user_{uuid.uuid4().hex}"
    password = "password123"

    # Sign up
    response = auth_api.signup(username, password)
    assert response.status_code == 201

    # Sign in
    response = auth_api.signin(username, password)
    assert response.status_code == 200

    token = response.json()["token"]

    # Generate file
    file_name = f"{uuid.uuid4().hex}.txt"
    content = uuid.uuid4().hex.encode("utf-8")

    # Generate folder
    folder_name = f"folder_{uuid.uuid4().hex}"

    # Upload file
    response = resource_api.upload(
        path=folder_name,
        file_name=file_name,
        content=content,
        token=token,
    )
    assert response.status_code == 201

    # Get resource information
    response = resource_api.get(
        path=(folder_name + "/" + file_name),
        token=token,
    )

    assert response.status_code == 200

    body = response.json()

    assert body == {
        "path": (folder_name + "/" + file_name),
        "name": file_name,
        "size": len(content),
        "type": "FILE",
    }