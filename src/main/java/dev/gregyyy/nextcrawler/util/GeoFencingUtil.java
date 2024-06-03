package dev.gregyyy.nextcrawler.util;

import dev.gregyyy.nextcrawler.model.Location;

import java.util.List;

public class GeoFencingUtil {

    public static boolean doesPointIntersectPolygon(List<Location> polygon, Location point) {
        int i, j;
        boolean c = false;
        for (i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            if (((polygon.get(i).lat() > point.lat()) != (polygon.get(j).lat() > point.lat())) &&
                    (point.lon() < (polygon.get(j).lon() - polygon.get(i).lon()) * (point.lat() - polygon.get(i).lat())
                            / (polygon.get(j).lat() - polygon.get(i).lat()) + polygon.get(i).lon())) {
                c = !c;
            }
        }
        return c;
    }

}
