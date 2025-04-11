from fastapi.testclient import TestClient
from main import app
from services.lcom4_calculator import calculate_lcom4

client = TestClient(app)

def test_calculate_lcom4():
    mock_class = _mock_java_class_with_fields_and_access()
    result = calculate_lcom4(mock_class)
    assert isinstance(result, float)
    assert result == 2.0

def _mock_java_class_with_fields_and_access():
    class MockMemberReference:
        def __init__(self, member):
            self.member = member
            self.qualifier = None

    class MockMethod:
        def __init__(self, accessed_fields):
            self.body = True
            self.parameters = []
            self._nodes = [(None, MockMemberReference(field)) for field in accessed_fields]

        def __iter__(self):
            return iter(self._nodes)

    class MockFieldDeclarator:
        def __init__(self, name):
            self.name = name

    class MockFieldDecl:
        def __init__(self, declarators):
            self.declarators = declarators

    class MockClass:
        name = "MockClass"
        methods = [MockMethod(["field1"]), MockMethod(["field1"])]
        fields = [MockFieldDecl([MockFieldDeclarator("field1")])]

    return MockClass()
