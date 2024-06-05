package dev.gregyyy.nextcrawler.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
public class Bike {

    private final String number;
    @Setter
    private long uid;
    @Setter
    private Location location;
    @Setter
    private Status status;
    @Setter
    private ZonedDateTime lastStateChange;

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

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}
