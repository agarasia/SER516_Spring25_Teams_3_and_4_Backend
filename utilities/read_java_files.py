import os
import sys 
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))
from java_file_parser import JavaFileParser
import config


def read_files(repo_dir):
    if os.path.isdir(repo_dir):
        file_contents = {}
        for root, _, files in os.walk(repo_dir):
            for file_name in files:
                full_path = os.path.join(root, file_name)
                if JavaFileParser.is_valid(full_path):
                    _, ext = os.path.splitext(file_name)
                    if ext in config.white_list:
                        file_contents[full_path] = JavaFileParser.parse_file(full_path)
        return file_contents
    return {}