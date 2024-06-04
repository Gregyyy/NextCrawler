package dev.gregyyy.nextcrawler.crawler;

import dev.gregyyy.nextcrawler.api.element.City;
import dev.gregyyy.nextcrawler.api.element.Place;
import dev.gregyyy.nextcrawler.model.Bike;
import dev.gregyyy.nextcrawler.model.Location;
import dev.gregyyy.nextcrawler.model.Trip;
import dev.gregyyy.nextcrawler.util.GeoFencingUtil;

import java.util.List;

import static dev.gregyyy.nextcrawler.database.Prometheus.*;

public class PrometheusCrawler extends Crawler {

    private final List<Location> unifestSmall = List.of(
            new Location(49.00945, 8.41344), // left bottom
            new Location(49.01131, 8.41358), // left left middle
            new Location(49.01130, 8.41479), // left middle
            new Location(49.01288, 8.41493), // left top
            new Location(49.01301, 8.41701), // right top
            new Location(49.00914, 8.41632) // right bottom
    );

    @Override
    public void save(List<Place> places, City city, List<Bike> bikes, List<Trip> trips) {
        String[] cityLabels = {city.getUid(), city.getName(), city.getAlias()};

        citySetPointBikes.labels(cityLabels).set(city.getSetPointBikes());
        cityPlaces.labels(cityLabels).set(city.getNumberOfPlaces());
        cityBookedBikes.labels(cityLabels).set(city.getBookedBikes());
        cityAvailableBikes.labels(cityLabels).set(city.getAvailableBikes());

        for (Place place : places) {
            String[] placeLabels = {place.getName(), place.isBike() + "", place.getLatitude() + "",
                    place.getLongitude() + ""};

            placeBookedBikes.labels(placeLabels).set(place.getNumberOfBookedBikes());
            placeBikes.labels(placeLabels).set(place.getNumberOfBikes());
            placeBikesAvailableToRent.labels(placeLabels).set(place.getNumberOfBikesAvailableToRent());
            placeBikeRacks.labels(placeLabels).set(place.getNumberOfBikeRacks());
            placeFreeRacks.labels(placeLabels).set(place.getNumberOfFreeRacks());
            placeUid.labels(placeLabels).set(place.getUid());
        }

        int totalBikes = 0;
        int availableBikes = 0;
        int reservedBikes = 0;
        for (Bike bike : bikes) {
            String[] bikeLabels = {bike.getNumber()};

            bikeStatus.labels(bikeLabels).set(bike.getStatus().ordinal());
            bikeLat.labels(bikeLabels).set(bike.getLocation().lat());
            bikeLon.labels(bikeLabels).set(bike.getLocation().lon());
            bikeUid.labels(bikeLabels).set(bike.getUid());

            if (GeoFencingUtil.doesPointIntersectPolygon(unifestSmall,
                    new Location(bike.getLocation().lat(), bike.getLocation().lon()))) {
                switch (bike.getStatus()) {
                    case AVAILABLE -> {
                        totalBikes++;
                        availableBikes++;
                    }
                    case RESERVED -> {
                        totalBikes++;
                        reservedBikes++;
                    }
                }
            }
        }

        geoFenceTotalBikes.labels("unifest_small").set(totalBikes);
        geoFenceAvailableBikes.labels("unifest_small").set(availableBikes);
        geoFenceReservedBikes.labels("unifest_small").set(reservedBikes);

        for (Trip trip : trips) {
            if (trip.getEndLocation() == null) {
                continue;
            }

            String[] tripLabels = {trip.getBikeNumber(), trip.getStartUid() + "", trip.getEndUid() + ""};

            tripDurationInMinutes.labels(tripLabels).set(trip.getDurationInMinutes());
            tripStartLat.labels(tripLabels).set(trip.getStartLocation().lat());
            tripStartLon.labels(tripLabels).set(trip.getStartLocation().lon());
            tripEndLat.labels(tripLabels).set(trip.getEndLocation().lat());
            tripEndLon.labels(tripLabels).set(trip.getEndLocation().lon());
        }
    }
}
