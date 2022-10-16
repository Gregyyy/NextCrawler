package dev.gregyyy.nextcrawler;

public class NextCrawler {

    private static final String VERSION = "0.3.0";

    public static void main(String[] args) {
        System.out.println("Starting NextCrawler v" + VERSION);

        Influx influx = null;
        switch (System.getenv("CONNECTION_MODE")) {
            case "CLOUD" -> {
                String url = System.getenv("URL");
                String token = System.getenv("TOKEN");
                String org = System.getenv("ORG");
                String bucket = System.getenv("BUCKET");

                influx = new Influx(url, token, org, bucket);
            }
            case "LOCAL" -> {
                String url = System.getenv("URL");
                String username = System.getenv("USERNAME");
                String password = System.getenv("PASSWORD");
                String bucket = System.getenv("BUCKET");
                String org = System.getenv("ORG");

                influx = new Influx(url, username, password, bucket, org);
            }
        }

        int queryInterval = Integer.parseInt(System.getenv("QUERY_INTERVAL"));

        new Crawler(influx).start("fg", queryInterval);
    }

}
