from fastapi.testclient import TestClient
from main import app
from services.lcomhs_calculator import calculate_lcomhs

client = TestClient(app)

def test_calculate_lcomhs():
    mock_class = _mock_java_class_with_methods()
    result = calculate_lcomhs(mock_class)
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
