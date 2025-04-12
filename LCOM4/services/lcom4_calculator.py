from typing import List, Dict, Any, Set
import javalang

def calculate_lcom4(java_class: Any) -> float:
    """
    Computes LCOM4 for a single Java class.

    :param java_class: A parsed Java class node/object.
    :return: A float representing the LCOM4 value. 
    """
    # get the class methods
    methods = _get_methods(java_class)
    
    # Map each method to the set of fields it accesses
    method_fields_map = {}

    class_fields = _get_class_fields(java_class)
    for method in methods:
        accessed_fields = _get_accessed_fields(method, class_fields)
        method_fields_map[method] = accessed_fields
    
    # Build an adjacency list: methods are connected if they share common fields
    adjacency_list = {m: set() for m in methods}
    for m1 in methods:
        for m2 in methods:
            if m1 == m2:
                continue
            if method_fields_map[m1].intersection(method_fields_map[m2]):
                adjacency_list[m1].add(m2)
                adjacency_list[m2].add(m1)
                
    # Count connected components using adjacency_list
    connected_components = _count_connected_components(adjacency_list)
    
    # By definitions: LCOM4 = number_of_connected_components
    lcom4_value = connected_components
    
    return float(lcom4_value)

def _get_methods(java_class: Any) -> List[Any]:
    """
    Extract all method declarations from the given java_class node.
    """
    return getattr(java_class, 'methods', [])

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


def _count_connected_components(adjacency_list: Dict[Any, set]) -> int:
    """
    Uses DFS to count connected components in an undirected graph.

    :param adjacency_list: dict of (node, set) of connected nodes
    :return: The number of connected components in the graph
    """
    visited = set()
    components_count = 0
    
    for node in adjacency_list:
        if node not in visited:
            components_count += 1
            _dfs(node, adjacency_list, visited)
    
    return components_count

def _dfs(start_node: Any, adjacency_list: Dict[Any, set], visited: set):
    stack = [start_node]
    while stack:
        node = stack.pop()
        if node not in visited:
            visited.add(node)
            # Add neighbors that haven't been visited
            stack.extend(adjacency_list[node] - visited)
