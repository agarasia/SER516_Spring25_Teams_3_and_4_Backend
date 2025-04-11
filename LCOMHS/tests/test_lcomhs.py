from fastapi.testclient import TestClient
from main import app
from services.lcomhs_calculator import calculate_lcomhs

client = TestClient(app)

def test_calculate_lcomhs():
    mock_class = _mock_java_class_with_fields_and_access()
    result = calculate_lcomhs(mock_class)
    assert isinstance(result, float)
    assert 0.0 <= result <= 2.0 

def _mock_java_class_with_fields_and_access():
    class MockMemberReference:
        def __init__(self, member):
            self.member = member
            self.qualifier = None

    class MockMethod:
        def __init__(self, accessed_fields):
            self.body = True
            self._nodes = [(None, MockMemberReference(field)) for field in accessed_fields]
            self.parameters = []

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
