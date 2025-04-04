import pytest
import math
from unittest.mock import patch, MagicMock
from DefectScore.services.defect_score_calculator import compute_defect_score_from_github

@pytest.mark.parametrize("repo_url, token", [
    ("https://github.com/owner/repo", None),
    ("https://github.com/owner/private-repo", "ghp_exampleToken123"),
])
def test_compute_defect_score_no_issues(repo_url, token, monkeypatch):
    """
    Tests the scenario where GitHub returns an empty list of issues (or none matching).
    """

    # Mock 'requests.get' to return an empty list of issues
    def mock_requests_get(url, params=None, headers=None):
        mock_resp = MagicMock()
        mock_resp.status_code = 200
        mock_resp.json.return_value = []  # no issues
        return mock_resp

    # Mock 'fetch_label_mapping_from_firebase' to return a custom map
    def mock_fetch_label_map(repo_url):
        return {"bug": 3, "critical": 5}  # example map

    monkeypatch.setattr("DefectScore.services.defect_score_calculator.requests.get", mock_requests_get)
    monkeypatch.setattr("DefectScore.services.defect_score_calculator.fetch_label_mapping_from_firebase", mock_fetch_label_map)

    result = compute_defect_score_from_github(repo_url, token)
    assert result["total_defects"] == 0
    assert result["weighted_average_severity"] == 0
    assert result["min_severity"] == 0
    assert result["max_severity"] == 0
    assert math.isclose(result["std_dev_severity"], 0, abs_tol=1e-9)

def test_compute_defect_score_with_some_issues(monkeypatch):
    """
    Tests a scenario with multiple issues, some labeled, some not.
    """
    repo_url = "https://github.com/owner/repo"
    token = None

    issues_data = [
        {
            "labels": [{"name": "bug"}],
            "title": "Issue #1"
        },
        {
            "labels": [{"name": "bug"}, {"name": "critical"}],
            "title": "Issue #2"
        },
        {
            "labels": [{"name": "enhancement"}],
            "title": "Issue #3"
        }
    ]

    def mock_requests_get(url, params=None, headers=None):
        mock_resp = MagicMock()
        mock_resp.status_code = 200
        mock_resp.json.return_value = issues_data
        return mock_resp

    def mock_fetch_label_map(repo_url):
        # user-defined or default map
        return {
            "bug": 3,
            "critical": 5,
            "minor": 1
        }

    monkeypatch.setattr("DefectScore.services.defect_score_calculator.requests.get", mock_requests_get)
    monkeypatch.setattr("DefectScore.services.defect_score_calculator.fetch_label_mapping_from_firebase", mock_fetch_label_map)

    result = compute_defect_score_from_github(repo_url, token)
    assert result["total_defects"] == 2
    assert result["weighted_average_severity"] == 4
    assert result["min_severity"] == 3
    assert result["max_severity"] == 5
    assert result["std_dev_severity"] == 1

def test_compute_defect_score_no_label_map(monkeypatch):
    """
    Test scenario when fetch_label_mapping_from_firebase returns an empty map,
    so the default map is used (bug=2, minor=2, major=4, critical=5, etc.).
    """
    repo_url = "https://github.com/owner/repo"
    token = None

    issues_data = [
        {"labels": [{"name": "bug"}]},
        {"labels": [{"name": "critical"}]}
    ]

    def mock_requests_get(url, params=None, headers=None):
        mock_resp = MagicMock()
        mock_resp.status_code = 200
        mock_resp.json.return_value = issues_data
        return mock_resp

    def mock_fetch_label_map(repo_url):
        return {}

    monkeypatch.setattr("DefectScore.services.defect_score_calculator.requests.get", mock_requests_get)
    monkeypatch.setattr("DefectScore.services.defect_score_calculator.fetch_label_mapping_from_firebase", mock_fetch_label_map)

    result = compute_defect_score_from_github(repo_url, token)

    assert result["total_defects"] == 2
    assert result["weighted_average_severity"] == 3.5
    assert result["min_severity"] == 2
    assert result["max_severity"] == 5