package dev.gregyyy.nextcrawler.database;

import io.prometheus.client.Gauge;

public class Prometheus {

    public static final String[] cityLabels = {"uid", "name", "alias"};
    public static final Gauge citySetPointBikes = Gauge.build()
            .name("nextcrawler_city_set_point_bikes")
            .help("Set point of bikes in city")
            .labelNames(cityLabels)
            .register();
    public static final Gauge cityPlaces = Gauge.build()
            .name("nextcrawler_city_bikes")
            .help("Number of places in city")
            .labelNames(cityLabels)
            .register();
    public static final Gauge cityBookedBikes = Gauge.build()
            .name("nextcrawler_city_booked_bikes")
            .help("Number of booked bikes in city")
            .labelNames(cityLabels)
            .register();
    public static final Gauge cityAvailableBikes = Gauge.build()
            .name("nextcrawler_city_available_bikes")
            .help("Number of available bikes in city")
            .labelNames(cityLabels)
            .register();

    public static final String[] placeLabels = {"name", "isBike", "lat", "lon"};
    public static final Gauge placeBookedBikes = Gauge.build()
            .name("nextcrawler_place_booked_bikes")
            .help("Number of booked bikes at a place")
            .labelNames(placeLabels)
            .register();
    public static final Gauge placeBikes = Gauge.build()
            .name("nextcrawler_place_bikes")
            .help("Number of bikes at a place")
            .labelNames(placeLabels)
            .register();
    public static final Gauge placeBikesAvailableToRent = Gauge.build()
            .name("nextcrawler_place_bikes_available_to_rent")
            .help("Number of bikes available to rent at a place")
            .labelNames(placeLabels)
            .register();
    public static final Gauge placeBikeRacks = Gauge.build()
            .name("nextcrawler_place_bike_racks")
            .help("Number of bikes racks at a place")
            .labelNames(placeLabels)
            .register();
    public static final Gauge placeFreeRacks = Gauge.build()
            .name("nextcrawler_place_free_racks")
            .help("Number of free bike racks at a place")
            .labelNames(placeLabels)
            .register();
    public static final Gauge placeUid = Gauge.build()
            .name("nextcrawler_place_uid")
            .help("Current uid of a place")
            .labelNames(placeLabels)
            .register();

    public static final String[] bikeLabels = {"name"};
    public static final Gauge bikeStatus = Gauge.build()
            .name("nextcrawler_bike_status")
            .help("Status of a bike")
            .labelNames(bikeLabels)
            .register();
    public static final Gauge bikeLat = Gauge.build()
            .name("nextcrawler_bike_lat")
            .help("Latitude of position of a bike")
            .labelNames(bikeLabels)
            .register();
    public static final Gauge bikeLon = Gauge.build()
            .name("nextcrawler_bike_lon")
            .help("Longitude of position of a bike")
            .labelNames(bikeLabels)
            .register();
    public static final Gauge bikeUid = Gauge.build()
            .name("nextcrawler_bike_uid")
            .help("Current uid of a bike")
            .labelNames(bikeLabels)
            .register();
    public static final Gauge bikeStateDuration = Gauge.build()
            .name("nextcrawler_bike_state_duration")
            .help("Duration of current state of a bike")
            .labelNames(bikeLabels)
            .register();

    public static final String[] tripLabels = {"bikeNumber", "startUid", "endUid", "startDate", "endDate"};
    public static final Gauge tripDurationInMinutes = Gauge.build()
            .name("nextcrawler_trip_duration_in_minutes")
            .help("Duration in minutes of trip")
            .labelNames(tripLabels)
            .register();
    public static final Gauge tripStartLat = Gauge.build()
            .name("nextcrawler_trip_start_lat")
            .help("Start latitude of trip")
            .labelNames(tripLabels)
            .register();
    public static final Gauge tripStartLon = Gauge.build()
            .name("nextcrawler_trip_start_lon")
            .help("Start longitude of trip")
            .labelNames(tripLabels)
            .register();
    public static final Gauge tripEndLat = Gauge.build()
            .name("nextcrawler_trip_end_lat")
            .help("End latitude of trip")
            .labelNames(tripLabels)
            .register();
    public static final Gauge tripEndLon = Gauge.build()
            .name("nextcrawler_trip_end_lon")
            .help("End longitude of trip")
            .labelNames(tripLabels)
            .register();

    public static final String[] geoFenceLabels = {"name"};
    public static final Gauge geoFenceTotalBikes = Gauge.build()
            .name("nextcrawler_geo_fence_total_bikes")
            .help("Total number of bikes in geo fence")
            .labelNames(geoFenceLabels)
            .register();
    public static final Gauge geoFenceAvailableBikes = Gauge.build()
            .name("nextcrawler_geo_fence_available_bikes")
            .help("Number of available (not reserved) bikes in geo fence")
            .labelNames(geoFenceLabels)
            .register();
    public static final Gauge geoFenceReservedBikes = Gauge.build()
            .name("nextcrawler_geo_fence_reserved_bikes")
            .help("Number of reserved bikes in geo fence")
            .labelNames(geoFenceLabels)
            .register();


    private Prometheus() {}

}
