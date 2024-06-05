package dev.gregyyy.nextcrawler.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Date;

@Getter
public class Trip {

    private final String bikeNumber;
    private final long startUid;
    @Setter
    private long endUid;
    private final ZonedDateTime startDate;
    private final Location startLocation;
    @Setter
    private Location endLocation;
    @Setter
    private int durationInMinutes;
    @Setter
    private ZonedDateTime endDate;


    public Trip(String bikeNumber, long startUid, ZonedDateTime startDate, Location startLocation) {
        this.bikeNumber = bikeNumber;
        this.startUid = startUid;
        this.startLocation = startLocation;
        this.startDate = startDate;
    }

}
