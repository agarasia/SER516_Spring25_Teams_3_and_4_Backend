from datetime import datetime
from datetime import timezone

def wrap_with_timestamp(data):
    return {
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "data": data
    }