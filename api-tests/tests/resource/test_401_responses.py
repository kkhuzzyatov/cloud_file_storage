import pytest


@pytest.mark.parametrize(
    "api_call",
    [
        lambda api: api.list(),
        lambda api: api.delete(),
        lambda api: api.download(),
        lambda api: api.move(),
        lambda api: api.search(),
        lambda api: api.create(),
    ],
)
def test_resource_requires_authorization(resource_api, api_call):
    response = api_call(resource_api)

    assert response.status_code == 401