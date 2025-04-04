from fastapi.testclient import TestClient
from main import app
from services.lcom4_calculator import calculate_lcom4

client = TestClient(app)

def test_calculate_lcom4():
    mock_class = _mock_java_class_with_methods()
    result = calculate_lcom4(mock_class)
    # assertion
    assert isinstance(result, float)

def _mock_java_class_with_methods():

    class MockMethod:
        body = []
        def __iter__(self):
            return iter([])

    class MockClass:
        name = "MockClass"
        methods = [MockMethod(), MockMethod()]

    return MockClass()
