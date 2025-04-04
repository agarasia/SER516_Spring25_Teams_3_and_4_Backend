import os
from typing import List
import javalang

def parse_java_files_in_dir(directory_path: str):
    """
    Recursively collects all .java files in the specified directory
    and parses them with javalang.
    :param directory_path: Path to the local directory with Java files.
    :return: A list of parsed javalang compilation units or class nodes.
    """
    parsed_classes = []
    for root, dirs, files in os.walk(directory_path):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                with open(file_path, "r", encoding="utf-8") as f:
                    source_code = f.read()
                    parsed_classes += parse_java_file_contents(source_code)

    return parsed_classes

def parse_java_file_contents(source_code: str):
    """
    Parse a single Java file contents (string) into javalang AST.
    :param source_code: Java file content as a string.
    :return: A list of class nodes from this single file.
    """
    parsed_classes = []
    try:
        tree = javalang.parse.parse(source_code)
        for path, node in tree:
            if hasattr(node, 'methods'):
                parsed_classes.append(node)
    except javalang.parser.JavaSyntaxError as e:
        print(f"Error parsing java source: {e}")
    return parsed_classes
