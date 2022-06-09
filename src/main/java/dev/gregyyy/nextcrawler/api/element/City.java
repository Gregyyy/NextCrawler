package dev.gregyyy.nextcrawler.api.element;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class City {

    @Getter
    public String uid;
    @Getter
    public String name;
    @Getter
    public String alias;
    @Getter
    @SerializedName("numPlaces")
    private int numberOfPlaces;
    @Getter
    private String refreshRate;
    @Getter
    @SerializedName("booked_bikes")
    private int bookedBikes;
    @Getter
    @SerializedName("set_point_bikes")
    private int setPointBikes;
    @Getter
    @SerializedName("available_bikes")
    private int availableBikes;
    @Getter
    private int[] bikeTypes;
    @Getter
    private Place[] places;

}
