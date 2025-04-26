import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))
import config


class JavaFileParser:
    
    @staticmethod
    def parse_file(file_path: str):
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                return file.read()
        except FileNotFoundError:
            raise FileNotFoundError(f"File {file_path} not found.")
        except Exception as e:
            raise ValueError(f"Error reading file {file_path}: {str(e)}")

    @staticmethod
    def is_valid(file_path: str):

        for dir in config.ignored_directories:
            if dir in file_path:
                return False
        return True