package dev.gregyyy.nextcrawler.crawler;

import dev.gregyyy.nextcrawler.api.element.City;
import dev.gregyyy.nextcrawler.api.element.Place;
import dev.gregyyy.nextcrawler.model.Bike;
import dev.gregyyy.nextcrawler.model.Location;
import dev.gregyyy.nextcrawler.model.Trip;
import dev.gregyyy.nextcrawler.util.GeoFencingUtil;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    private final List<Location> unifestBig = List.of(
            new Location(49.00902, 8.41006), // Kronenplatz
            new Location(49.01001, 8.40974), // Kant-Gymnasium
            new Location(49.01171, 8.41164), // Straße vor Gerthsen
            new Location(49.01421, 8.41288), // Fasanengarten
            new Location(49.01413, 8.41936), // Infobau
            new Location(49.01123, 8.41916), // InformatiKOM
            new Location(49.00849, 8.41787) // Durlacher Tor
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

        for (Bike bike : bikes) {
            String[] bikeLabels = {bike.getNumber()};

            bikeStatus.labels(bikeLabels).set(bike.getStatus().ordinal());
            bikeLat.labels(bikeLabels).set(bike.getLocation().lat());
            bikeLon.labels(bikeLabels).set(bike.getLocation().lon());
            bikeUid.labels(bikeLabels).set(bike.getUid());
            long stateDuration = Duration.between(bike.getLastStateChange(), ZonedDateTime.now()).toMinutes();
            bikeStateDuration.labels(bikeLabels).set(stateDuration);
        }

        setCountGeoFenceGauges(bikes, unifestSmall, "unifest_small");
        setCountGeoFenceGauges(bikes, unifestBig, "unifest_big");

        tripDurationInMinutes.clear();
        tripStartLat.clear();
        tripStartLon.clear();
        tripEndLat.clear();
        tripEndLon.clear();

        for (Trip trip : trips) {
            if (trip.getEndLocation() == null) {
                continue;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            String[] tripLabels = {trip.getBikeNumber(), trip.getStartUid() + "", trip.getEndUid() + "",
                    formatter.format(trip.getStartDate()), formatter.format(trip.getEndDate())};

            tripDurationInMinutes.labels(tripLabels).set(trip.getDurationInMinutes());
            tripStartLat.labels(tripLabels).set(trip.getStartLocation().lat());
            tripStartLon.labels(tripLabels).set(trip.getStartLocation().lon());
            tripEndLat.labels(tripLabels).set(trip.getEndLocation().lat());
            tripEndLon.labels(tripLabels).set(trip.getEndLocation().lon());
        }
    }

    private void setCountGeoFenceGauges(List<Bike> bikes, List<Location> polygon, String name) {
        int totalBikes = 0;
        int availableBikes = 0;
        int reservedBikes = 0;
        for (Bike bike : bikes) {
            if (GeoFencingUtil.doesPointIntersectPolygon(polygon,
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

        geoFenceTotalBikes.labels(name).set(totalBikes);
        geoFenceAvailableBikes.labels(name).set(availableBikes);
        geoFenceReservedBikes.labels(name).set(reservedBikes);
    }

}
