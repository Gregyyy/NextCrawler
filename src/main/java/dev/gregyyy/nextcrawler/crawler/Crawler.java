package dev.gregyyy.nextcrawler.crawler;

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

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Crawler {

    private static final String API_HOST = "https://api.nextbike.net/";

    private final OkHttpClient client;
    private final Gson gson;

    private final ScheduledExecutorService executor;
    private volatile ScheduledFuture<?> task;

    private final List<Bike> bikes = new ArrayList<>();
    private final List<Trip> trips = new ArrayList<>();

    public Crawler() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public abstract void save(List<Place> places, City city, List<Bike> bikes, List<Trip> trips);

    public void start(String domains, int interval) {
        if (task != null) {
            throw new RuntimeException("Already started");
        }

        task = executor.scheduleWithFixedDelay(() -> {
            try {
                System.out.println("Crawling...");

                MapsResult result = crawl(domains);
                City karlsruhe = result.getCountries()[0].getCities()[0];
                List<Place> places = List.of(karlsruhe.getPlaces());

                saveInMemory(places);
                save(places, karlsruhe, bikes, trips);
                trips.removeIf(trip -> trip.getEndLocation() != null);

                System.out.println("Saved");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, interval, TimeUnit.MINUTES);
    }

    public void stop() {
        task.cancel(true);
        executor.shutdown();
    }

    public MapsResult crawl(String domains) throws IOException {
        Request request = new Request.Builder().url(API_HOST + "maps/nextbike-live.json?domains=" + domains).build();

        try (Response response = client.newCall(request).execute()) {
            String content = Objects.requireNonNull(response.body()).string();

            return gson.fromJson(content, MapsResult.class);
        }
    }

    private void saveInMemory(List<Place> places) {
        saveBikes(places);
        saveTrips(new Date());
    }

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
                ZonedDateTime startDate = trip.get().getStartDate().toInstant().atZone(ZoneId.of("Europe/Berlin"));
                trip.get().setDurationInMinutes((int) Duration.between(startDate, ZonedDateTime.now()).toMinutes());
            } else if (trip.isEmpty() && bike.getStatus() == Status.UNAVAILABLE) {
                trips.add(new Trip(bike.getNumber(), bike.getUid(), date, bike.getLocation()));
            }
        }
    }

}
