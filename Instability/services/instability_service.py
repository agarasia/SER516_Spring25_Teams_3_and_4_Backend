# from services.mongo_service import store_instability_data_in_mongo

def compute_instability(a_data, e_data):
    a_dict = {d["class_name"]: d["score"] for d in a_data}
    e_dict = {d["class_name"]: d["score"] for d in e_data}

    result = []
    for class_name in set(a_dict) | set(e_dict):
        afferent = a_dict.get(class_name, 0)
        efferent = e_dict.get(class_name, 0)
        total = afferent + efferent
        instability = round(efferent / total, 4) if total != 0 else 0.0
        result.append({
            "class_name": class_name,
            "instability": instability
        })
    return result


def process_instability(input_data):
    history = []
    for a_entry, e_entry in zip(
        input_data["afferent"]["afferent_history"],
        input_data["efferent"]["efferent_history"]
    ):
        history.append({
            "timestamp": a_entry["timestamp"],
            "data": compute_instability(a_entry["data"], e_entry["data"])
        })

    current = {
        "timestamp": input_data["afferent"]["current_afferent"]["timestamp"],
        "data": compute_instability(
            input_data["afferent"]["current_afferent"]["data"],
            input_data["efferent"]["current_efferent"]["data"]
        )
    }

    result = {
        "instability_history": history,
        "current_instability": current
    }

    # store_instability_data_in_mongo(result) 
    return result
