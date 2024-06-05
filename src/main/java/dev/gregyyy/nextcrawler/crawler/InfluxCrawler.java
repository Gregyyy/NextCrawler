package dev.gregyyy.nextcrawler.crawler;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import dev.gregyyy.nextcrawler.api.element.City;
import dev.gregyyy.nextcrawler.api.element.Place;
import dev.gregyyy.nextcrawler.database.Influx;
import dev.gregyyy.nextcrawler.model.Bike;
import dev.gregyyy.nextcrawler.model.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InfluxCrawler extends Crawler {

    private final Influx influx;

    public InfluxCrawler(Influx influx) {
        this.influx = influx;
    }

    @Override
    public void save(List<Place> places, City city, List<Bike> bikes, List<Trip> trips) {
        Date date = new Date();

        List<Point> points = new ArrayList<>();

        points.add(Point.measurement("city")
                .time(date.getTime(), WritePrecision.MS)
                .addField("setPointBikes", city.getSetPointBikes())
                .addField("numberOfPlaces", city.getNumberOfPlaces())
                .addField("bookedBikes", city.getBookedBikes())
                .addField("availableBikes", city.getAvailableBikes()));

        for (Place place : places) {
            points.add(Point.measurement("places")
                    .time(date.getTime(), WritePrecision.MS)
                    .addTag("name", place.getName())
                    .addField("uid", place.getUid())
                    .addField("name", place.getName())
                    .addField("bike", place.isBike())
                    .addField("lat", place.getLatitude())
                    .addField("lon", place.getLongitude())
                    .addField("numberOfBookedBikes", place.getNumberOfBookedBikes())
                    .addField("numberOfBikes", place.getNumberOfBikes())
                    .addField("numberOfBikesAvailableToRent", place.getNumberOfBikesAvailableToRent())
                    .addField("numberOfBikeRacks", place.getNumberOfBikeRacks())
                    .addField("numberOfFreeRacks", place.getNumberOfBikeRacks()));
        }

        for (Bike bike : bikes) {
            points.add(Point.measurement("bikes")
                    .time(date.getTime(), WritePrecision.MS)
                    .addTag("name", bike.getNumber())
                    .addField("status", bike.getStatus().toString())
                    .addField("uid", bike.getUid())
                    .addField("lat", bike.getLocation().lat())
                    .addField("lon", bike.getLocation().lon()));
        }

        for (Trip trip : trips) {
            if (trip.getEndLocation() != null) {
                points.add(Point.measurement("trips")
                        .time(trip.getStartDate().toInstant(), WritePrecision.MS)
                        .addTag("bikeNumber", trip.getBikeNumber())
                        .addField("durationInMinutes", trip.getDurationInMinutes())
                        .addField("startUid", trip.getStartUid())
                        .addField("endUid", trip.getEndUid())
                        .addField("startLat", trip.getStartLocation().lat())
                        .addField("startLon", trip.getStartLocation().lon())
                        .addField("endLat", trip.getEndLocation().lat())
                        .addField("endLon", trip.getEndLocation().lon()));
            }
        }

        influx.write(points);
    }
}
