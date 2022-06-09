package dev.gregyyy.nextcrawler.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class Trip {

    @Getter
    private final String bikeNumber;
    @Getter
    private final long startUid;
    @Getter
    @Setter
    private long endUid;
    @Getter
    private final Date startDate;
    @Getter
    private final Location startLocation;
    @Getter
    @Setter
    private Location endLocation;
    @Getter
    @Setter
    private int durationInMinutes;

    public Trip(String bikeNumber, long startUid, Date startDate, Location startLocation) {
        this.bikeNumber = bikeNumber;
        this.startUid = startUid;
        this.startLocation = startLocation;
        this.startDate = startDate;
    }

}
