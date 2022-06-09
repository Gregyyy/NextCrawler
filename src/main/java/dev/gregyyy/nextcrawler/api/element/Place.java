package dev.gregyyy.nextcrawler.api.element;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class Place {

    @Getter
    private long uid;
    @Getter
    @SerializedName("lat")
    private double latitude;
    @Getter
    @SerializedName("lng")
    private double longitude;
    @Getter
    private boolean bike;
    @Getter
    private String name;
    @Getter
    private boolean spot;
    @Getter
    @SerializedName("booked_bikes")
    private int numberOfBookedBikes;
    @Getter
    @SerializedName("bikes")
    private int numberOfBikes;
    @Getter
    @SerializedName("bikes_available_to_rent")
    private int numberOfBikesAvailableToRent;
    @Getter
    @SerializedName("bike_racks")
    private int numberOfBikeRacks;
    @Getter
    @SerializedName("free_racks")
    private int numberOfFreeRacks;
    @Getter
    private boolean maintenance;
    @Getter
    @SerializedName("bike_list")
    private Bike[] bikes;
    @Getter
    private int[] bikeTypes;

}
