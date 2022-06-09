package dev.gregyyy.nextcrawler.api.element;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class Bike {

    @Getter
    private String number;
    @Getter
    private int bikeType;
    @Getter
    @SerializedName("lock_types")
    private String[] lockTypes;
    @Getter
    private boolean active;
    @Getter
    private String state;
    @Getter
    @SerializedName("electric_lock")
    private boolean hasElectricLock;
    @Getter
    private double pedelecBattery;
    @Getter
    private double batteryPack;
}
