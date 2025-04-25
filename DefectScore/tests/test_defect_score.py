# import pytest
# import math
# from unittest.mock import patch, MagicMock
# from services.defect_score_calculator import compute_defect_score_from_github

# @pytest.mark.parametrize("repo_url, token", [
#     ("https://github.com/owner/repo", None),
#     ("https://github.com/owner/private-repo", "ghp_exampleToken123"),
# ])
# def test_compute_defect_score_no_issues(repo_url, token, monkeypatch):
#     def mock_requests_get(url, params=None, headers=None):
#         mock_resp = MagicMock()
#         mock_resp.status_code = 200
#         mock_resp.json.return_value = []
#         return mock_resp

#     def mock_fetch_label_map(repo_url):
#         return [{"key": "bug", "value": 3}, {"key": "critical", "value": 5}]

#     monkeypatch.setattr("services.defect_score_calculator.requests.get", mock_requests_get)
#     monkeypatch.setattr("services.defect_score_calculator.fetch_label_mapping_from_mongo", mock_fetch_label_map)

#     result = compute_defect_score_from_github(repo_url, token)
#     assert result["total_defects"] == 0
#     assert result["weighted_average_severity"] == 0
#     assert result["min_severity"] == 0
#     assert result["max_severity"] == 0
#     assert math.isclose(result["std_dev_severity"], 0, abs_tol=1e-9)

# def test_compute_defect_score_with_some_issues(monkeypatch):
#     repo_url = "https://github.com/owner/repo"
#     token = None

#     issues_data = [
#         {"labels": [{"name": "bug"}]},
#         {"labels": [{"name": "bug"}, {"name": "critical"}]},
#         {"labels": [{"name": "enhancement"}]},
#     ]

#     def mock_requests_get(url, params=None, headers=None):
#         mock_resp = MagicMock()
#         mock_resp.status_code = 200
#         mock_resp.json.return_value = issues_data
#         return mock_resp

#     def mock_fetch_label_map(repo_url):
#         return [{"key": "bug", "value": 3}, {"key": "critical", "value": 5}]

#     monkeypatch.setattr("services.defect_score_calculator.requests.get", mock_requests_get)
#     monkeypatch.setattr("services.defect_score_calculator.fetch_label_mapping_from_mongo", mock_fetch_label_map)

#     result = compute_defect_score_from_github(repo_url, token)
#     assert result["total_defects"] == 2
#     assert result["weighted_average_severity"] == 4
#     assert result["min_severity"] == 3
#     assert result["max_severity"] == 5
#     assert result["std_dev_severity"] == 1

# def test_compute_defect_score_no_label_map(monkeypatch):
#     repo_url = "https://github.com/owner/repo"
#     token = None

#     issues_data = [
#         {"labels": [{"name": "bug"}]},
#         {"labels": [{"name": "critical"}]}
#     ]

#     def mock_requests_get(url, params=None, headers=None):
#         mock_resp = MagicMock()
#         mock_resp.status_code = 200
#         mock_resp.json.return_value = issues_data
#         return mock_resp

#     def mock_fetch_label_map(repo_url):
#         return [
#             {"key": "bug", "value": 2},
#             {"key": "critical", "value": 4} 
#         ]

#     monkeypatch.setattr("services.defect_score_calculator.requests.get", mock_requests_get)
#     monkeypatch.setattr("services.defect_score_calculator.fetch_label_mapping_from_mongo", mock_fetch_label_map)

#     result = compute_defect_score_from_github(repo_url, token)
#     assert result["total_defects"] == 2
#     assert result["weighted_average_severity"] == 3.0
#     assert result["min_severity"] == 2
#     assert result["max_severity"] == 4
#     assert result["std_dev_severity"] == 1.0
