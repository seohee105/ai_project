from collections import deque
import math

class GPSSmoother:
    def __init__(self, window_size=5, max_jump_meters=100):
        self.window_size = window_size
        self.max_jump_meters = max_jump_meters
        self.lat_window = deque(maxlen=window_size)
        self.lng_window = deque(maxlen=window_size)
        self.last_valid_lat = None
        self.last_valid_lng = None

    def haversine_distance(self, lat1, lng1, lat2, lng2) -> float:
        R = 6371000
        phi1, phi2 = math.radians(lat1), math.radians(lat2)
        dphi = math.radians(lat2 - lat1)
        dlambda = math.radians(lng2 - lng1)
        a = math.sin(dphi/2)**2 + math.cos(phi1) * math.cos(phi2) * math.sin(dlambda/2)**2
        return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

    def is_valid_point(self, lat: float, lng: float) -> bool:
        if self.last_valid_lat is None:
            return True
        distance = self.haversine_distance(
            self.last_valid_lat, self.last_valid_lng, lat, lng
        )
        return distance <= self.max_jump_meters

    def smooth(self, lat: float, lng: float) -> tuple:
        if self.is_valid_point(lat, lng):
            self.lat_window.append(lat)
            self.lng_window.append(lng)
            self.last_valid_lat = lat
            self.last_valid_lng = lng

        smoothed_lat = sum(self.lat_window) / len(self.lat_window)
        smoothed_lng = sum(self.lng_window) / len(self.lng_window)
        return smoothed_lat, smoothed_lng