package dev.gregyyy.nextcrawler.crawler;

import dev.gregyyy.nextcrawler.api.element.City;
import dev.gregyyy.nextcrawler.api.element.MapsResult;
import dev.gregyyy.nextcrawler.api.element.Place;
import dev.gregyyy.nextcrawler.model.Bike;
import dev.gregyyy.nextcrawler.model.Trip;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CrawlerTest {

    @Test
    void crawl() throws IOException {
        Crawler crawler = new Crawler() {
            @Override
            public void save(List<Place> places, City city, List<Bike> bikes, List<Trip> trips) {}
        };

        MapsResult result = crawler.crawl("fg");
        assertNotNull(result);
    }
}