import os
import javalang
from collections import defaultdict
from typing import List,Dict,Any,Set
def _get_java_files(directory: any) -> list[any]:
    """
    Extract all Java files from the Repository

    param: directory: location of local directory
    """
    java_files = []
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(root, file))

    return java_files

def _get_methods(java_class: Any) -> List[Any]:
    """
    Extract all method declarations from the given java_class node.
    """
    return getattr(java_class, 'methods', [])

def _get_num_of_fields(java_class: Any) -> Set[str]:
    """
    Extract all field declarations from the given java_class node.

    :param java_class: A javalang class.
    :return: A set of fields declared in the given java_class node.
    """
    fields = set()
    if hasattr(java_class, 'fields'):
        for field_decl in java_class.fields:
            for declarator in field_decl.declarators:
                fields.add(declarator.name)
    return len(fields)

def _parse_java_code(file_path: any) -> dict[any]:
    """
    Parse all Java files from the Repository

    param: directory: location of local directory
    return: dictionary of details for a Java class
    """

    with open(file_path, 'r', encoding='utf-8') as file:
        content = file.read()

    try:
        tree = javalang.parse.parse(content)
    except javalang.parser.JavaSyntaxError as e:
        print(f"Error parsing {file_path}: {e}")
        return None

    class_info = {'methods': set(), 'fields': set(), 'method_fields': defaultdict(set)}

    for path, node in tree.filter(javalang.tree.ClassDeclaration):
        # Process variables
        for field in node.fields:
            for declarator in field.declarators:
                class_info['fields'].add(declarator.name)

        # Process methods
        for method in node.methods:
            method_name = method.name
            class_info['methods'].add(method_name)

            # Analyze method body to find field access
            if method.body:
                for statement in method.body:
                    if isinstance(statement, javalang.tree.MemberReference):
                        if statement.member in class_info['fields']:
                            class_info['method_fields'][method.name].add(statement.member)

    return class_info


def calculate_lcomhs(class_info: Any) -> float:
    """
    Calculating LCOMHS metric using formula:
    LCOMHS = (M - sum(MF) / F) / (M - 1)
    """

    methods = _get_methods(class_info)
    count = 0
    class_fields = _get_class_fields(class_info)


    M = len(methods)
    F = len(class_fields)

    if M <= 1 or F == 0:
        return float(0)    # LCOMHS is undefined for these cases

    method_fields_map = {}

    for method in methods:
            accessed_fields = _get_accessed_fields(method, class_fields)
            method_fields_map[method] = accessed_fields

    for field in class_fields:
        for key in method_fields_map:
            if field in method_fields_map[key]:
                count += 1

    sum_MF = count
    lcomhs = 1- sum_MF / (M * F)
    return float(max(0, min(lcomhs, 2))).__round__(2)

def _extract_locals_and_params(method):
    """Collect local variable names + parameters in a method."""
    local_names = set()
    if hasattr(method, 'parameters'):
        for p in method.parameters:
            local_names.add(p.name)
    for _, node in method:
        if isinstance(node, javalang.tree.LocalVariableDeclaration):
            for decl in node.declarators:
                local_names.add(decl.name)
    return local_names

def _get_class_fields(java_class: Any) -> Set[str]:
    """
    Extract all field declarations from the given java_class node.

    :param java_class: A javalang class.
    :return: A set of fields declared in the given java_class node.
    """
    fields = set()
    if hasattr(java_class, 'fields'):
        for field_decl in java_class.fields:
            for declarator in field_decl.declarators:
                fields.add(declarator.name)
    return fields

def _get_accessed_fields(method: Any, all_fields: Set[str]) -> Set[str]:
    accessed = set()

    if not method.body:
        return accessed

    local_names = _extract_locals_and_params(method)

    for _, node in method:
        if isinstance(node, javalang.tree.MemberReference):
            qualifier = node.qualifier if node.qualifier else None
            member = node.member
            if qualifier in [None, "this"] and member in all_fields:
                accessed.add(member)
            else:
                if qualifier and qualifier not in local_names and qualifier in all_fields:
                    accessed.add(qualifier)

        elif isinstance(node, javalang.tree.MethodInvocation):
            qualifier = node.qualifier
            if qualifier and qualifier not in local_names and qualifier in all_fields:
                accessed.add(qualifier)

    return accessed

def process_directory(directory) -> any:
    """
    Process all Java files in a directory and calculate LCOMHS metrics

    param: directory: location of local directory
    return: average LCOMHS metric (if found) otherwise an error message
    """
    java_files = _get_java_files(directory)
    lcomhs_values = []

    for file in java_files:
        print(f"Processing: {file}")
        class_info = _parse_java_code(file)

        if not class_info:
            continue

        lcomhs = calculate_lcomhs(class_info)
        lcomhs_values.append(lcomhs)
    
    if lcomhs_values:
        avg_lcomhs = sum(lcomhs_values) / len(lcomhs_values)
        return avg_lcomhs
    else:
        return ("No valid Java files found or parsed.")