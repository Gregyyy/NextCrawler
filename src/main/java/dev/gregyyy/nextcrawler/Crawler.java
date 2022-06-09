package dev.gregyyy.nextcrawler;

import com.google.gson.Gson;
import dev.gregyyy.nextcrawler.api.element.City;
import dev.gregyyy.nextcrawler.api.element.MapsResult;
import dev.gregyyy.nextcrawler.api.element.Place;
import dev.gregyyy.nextcrawler.model.Bike;
import dev.gregyyy.nextcrawler.model.Status;
import dev.gregyyy.nextcrawler.model.Trip;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Crawler {

    private static final String API_HOST = "https://api.nextbike.net/";

    private final OkHttpClient client;
    private final Gson gson;
    private final Influx influx;

    private final ScheduledExecutorService executor;
    private volatile ScheduledFuture<?> task;

    public Crawler(Influx influx) {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.influx = influx;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start(String domains, int interval) {
        if (task != null) {
            throw new RuntimeException("Already started");
        }

        task = executor.scheduleWithFixedDelay(() -> {
            try {
                System.out.println("Crawling...");
                Date date = new Date();

                MapsResult result = crawl(domains);
                City karlsruhe = result.getCountries()[0].getCities()[0];
                Place[] places = karlsruhe.getPlaces();

                List<Point> points = new ArrayList<>();

                points.add(Point.measurement("city")
                        .time(date.getTime(), TimeUnit.MILLISECONDS)
                        .addField("setPointBikes", karlsruhe.getSetPointBikes())
                        .addField("numberOfPlaces", karlsruhe.getNumberOfPlaces())
                        .addField("bookedBikes", karlsruhe.getBookedBikes())
                        .addField("availableBikes", karlsruhe.getAvailableBikes()).build());

                for (Place place : places) {
                    points.add(Point.measurement("places")
                            .time(date.getTime(), TimeUnit.MILLISECONDS)
                            .tag("uid", place.getUid() + "")
                            .tag("name", place.getName())
                            .addField("name", place.getName())
                            .addField("bike", place.isBike())
                            .addField("lat", place.getLatitude())
                            .addField("lon", place.getLongitude())
                            .addField("numberOfBookedBikes", place.getNumberOfBookedBikes())
                            .addField("numberOfBikes", place.getNumberOfBikes())
                            .addField("numberOfBikesAvailableToRent", place.getNumberOfBikesAvailableToRent())
                            .addField("numberOfBikeRacks", place.getNumberOfBikeRacks())
                            .addField("numberOfFreeRacks", place.getNumberOfBikeRacks()).build());
                }

                saveBikes(List.of(places));

                for (Bike bike : bikes) {
                    points.add(Point.measurement("bikes")
                            .time(date.getTime(), TimeUnit.MILLISECONDS)
                            .tag("name", bike.getNumber())
                            .addField("status", bike.getStatus().toString())
                            .addField("uid", bike.getUid())
                            .addField("lat", bike.getLocation().lat())
                            .addField("lon", bike.getLocation().lon()).build());
                }

                saveTrips(date);

                for (Trip trip : trips) {
                    if (trip.getEndLocation() != null) {
                        points.add(Point.measurement("trips")
                                .time(trip.getStartDate().getTime(), TimeUnit.MILLISECONDS)
                                .tag("bikeNumber", trip.getBikeNumber())
                                .addField("durationInMinutes", trip.getDurationInMinutes())
                                .addField("startUid", trip.getStartUid())
                                .addField("endUid", trip.getEndUid())
                                .addField("startLat", trip.getStartLocation().lat())
                                .addField("startLon", trip.getStartLocation().lon())
                                .addField("endLat", trip.getEndLocation().lat())
                                .addField("endLon", trip.getEndLocation().lon()).build());
                    }
                }

                trips.removeIf(trip -> trip.getEndLocation() != null);

                influx.write(BatchPoints.builder().points(points).build());
                System.out.println("Saved");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, interval, TimeUnit.MINUTES);
    }

    List<Bike> bikes = new ArrayList<>();

    private void saveBikes(List<Place> places) {
        List<Bike> bikesFound = new ArrayList<>();

        for (Place place : places) {
            for (dev.gregyyy.nextcrawler.api.element.Bike apiBike : place.getBikes()) {
                if (bikes.stream().noneMatch(el -> el.getNumber().equals(apiBike.getNumber()))) {
                    bikes.add(new Bike(apiBike.getNumber()));
                }

                Optional<Bike> optionalBike = bikes.stream().filter(el -> el.getNumber().equals(apiBike.getNumber()))
                        .findFirst();
                if (optionalBike.isEmpty())
                    throw new IllegalStateException("Bike should exists");

                Bike bike = optionalBike.get();

                bike.setUid(place.getUid());
                bike.setLocation(place.getLatitude(), place.getLongitude());
                bike.setStatus(place.getNumberOfBikesAvailableToRent() > 0 ? Status.AVAILABLE : Status.RESERVED);

                bikesFound.add(bike);
            }
        }

        for (Bike bike : bikes) {
            if (!bikesFound.contains(bike)) {
                bike.setStatus(Status.UNAVAILABLE);
            }
        }
    }

    List<Trip> trips = new ArrayList<>();

    private void saveTrips(Date date) {
        for (Bike bike : bikes) {
            Optional<Trip> trip = trips.stream().filter(el -> el.getBikeNumber().equals(bike.getNumber())).findFirst();

            if (trip.isPresent() && trip.get().getEndLocation() != null) {
                System.out.println("error");
                throw new RuntimeException("Found ended trip");
            }

            if (trip.isPresent() && bike.getStatus() != Status.UNAVAILABLE) {
                trip.get().setEndLocation(bike.getLocation());
                trip.get().setEndUid(bike.getUid());
                LocalDateTime startDate = LocalDateTime.ofEpochSecond(
                        trip.get().getStartDate().getTime() / 1000, 0, ZoneOffset.UTC);
                trip.get().setDurationInMinutes(Duration.between(startDate, LocalDateTime.now()).toMinutesPart());
            } else if (trip.isEmpty() && bike.getStatus() == Status.UNAVAILABLE) {
                trips.add(new Trip(bike.getNumber(), bike.getUid(), date, bike.getLocation()));
            }
        }
    }

    public void stop() {
        task.cancel(true);
        executor.shutdown();
    }

    public MapsResult crawl(String domains) throws IOException {
        Request request = new Request.Builder().url(API_HOST + "maps/nextbike-live.json?domains=" + domains).build();

        try (Response response = client.newCall(request).execute()) {
            String content = response.body().string();

            return gson.fromJson(content, MapsResult.class);
        }
    }

}
