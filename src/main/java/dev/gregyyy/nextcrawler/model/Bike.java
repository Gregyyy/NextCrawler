package dev.gregyyy.nextcrawler.model;

import lombok.Getter;
import lombok.Setter;

public class Bike {

    @Getter
    private final String number;
    @Getter
    @Setter
    private long uid;
    @Getter
    @Setter
    private Location location;
    @Getter
    @Setter
    private Status status;

    public Bike(String number) {
        this.number = number;
    }

    public void setLocation(double lat, double lon) {
        this.location = new Location(lat, lon);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bike)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        return ((Bike) obj).getNumber().equals(this.number);
    }
}
