package dev.gregyyy.nextcrawler.api.element;

import lombok.Getter;

public class Country {

    @Getter
    private String name;
    @Getter
    private City[] cities;

}
