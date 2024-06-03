package dev.gregyyy.nextcrawler.util;

import dev.gregyyy.nextcrawler.model.Location;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeoFencingUtilTest {

    @Test
    void doesPointIntersectPolygonSimple() {
        List<Location> polygon = new ArrayList<>();
        polygon.add(new Location(1, 1));
        polygon.add(new Location(1, -1));
        polygon.add(new Location(-1, -1));
        polygon.add(new Location(-1, 1));
        boolean resultTrue = GeoFencingUtil.doesPointIntersectPolygon(polygon, new Location(0, 0));
        boolean resultFalse = GeoFencingUtil.doesPointIntersectPolygon(polygon, new Location(2, 2));

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    void doesPointIntersectPolygonComplex() {
        List<Location> polygon = new ArrayList<>();
        polygon.add(new Location(2, 1));
        polygon.add(new Location(4, 2));
        polygon.add(new Location(5, 4));
        polygon.add(new Location(4, 6));
        polygon.add(new Location(2, 7));
        polygon.add(new Location(0, 6));
        polygon.add(new Location(-1, 4));
        polygon.add(new Location(0, 2));

        boolean resultTrue = GeoFencingUtil.doesPointIntersectPolygon(polygon, new Location(2, 4));
        boolean resultFalse = GeoFencingUtil.doesPointIntersectPolygon(polygon, new Location(6, 6));


        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }
}