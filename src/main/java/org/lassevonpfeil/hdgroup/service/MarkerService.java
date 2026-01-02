package org.lassevonpfeil.hdgroup.service;

import org.lassevonpfeil.hdgroup.model.Marker;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkerService {
    private final Map<Integer, Marker> markers = new HashMap<>();

    public void setMarker(int pageIndex) {
        markers.put(pageIndex, new Marker(pageIndex));
    }

    public void removeMarker(int pageIndex) {
        markers.remove(pageIndex);
    }

    public boolean hasMarker(int pageIndex) {
        return markers.containsKey(pageIndex);
    }

    public List<Marker> getSortedMarkers() {
        return markers.values().stream()
                .sorted(Comparator.comparingInt(Marker::getStartIndex))
                .toList();
    }
}
