package dev.gregyyy.nextcrawler;

import dev.gregyyy.nextcrawler.crawler.InfluxCrawler;
import dev.gregyyy.nextcrawler.crawler.PrometheusCrawler;
import dev.gregyyy.nextcrawler.database.Influx;
import dev.gregyyy.nextcrawler.database.Prometheus;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;

public class NextCrawler {

    private static final String VERSION = "0.4.0";

    public static void main(String[] args) {
        System.out.println("Starting NextCrawler v" + VERSION);

        switch (System.getenv("DATABASE")) {
            case "INFLUX" -> startCrawlerWithInflux();
            case "PROMETHEUS" -> startPrometheusEndpoint();
            default -> System.exit(137);
        }
    }

    private static void startCrawlerWithInflux() {
        Influx influx = null;
        switch (System.getenv("CONNECTION_MODE")) {
            case "CLOUD" -> {
                String url = System.getenv("INFLUX_URL");
                String token = System.getenv("INFLUX_TOKEN");
                String org = System.getenv("INFLUX_ORG");
                String bucket = System.getenv("INFLUX_BUCKET");

                influx = new Influx(url, token, org, bucket);
            }
            case "LOCAL" -> {
                String url = System.getenv("INFLUX_URL");
                String username = System.getenv("INFLUX_USERNAME");
                String password = System.getenv("INFLUX_PASSWORD");
                String bucket = System.getenv("INFLUX_BUCKET");
                String org = System.getenv("INFLUX_ORG");

                influx = new Influx(url, username, password, bucket, org);
            }
            default -> System.exit(137);
        }

        int queryInterval = Integer.parseInt(System.getenv("QUERY_INTERVAL"));

        new InfluxCrawler(influx).start("fg", queryInterval);
    }

    private static void startPrometheusEndpoint() {
        int queryInterval = Integer.parseInt(System.getenv("QUERY_INTERVAL"));

        new PrometheusCrawler().start("fg", queryInterval);

        try {
            new HTTPServer(8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
