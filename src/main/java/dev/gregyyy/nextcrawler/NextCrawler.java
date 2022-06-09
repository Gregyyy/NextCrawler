package dev.gregyyy.nextcrawler;

public class NextCrawler {

    public static void main(String[] args) {
        String host = System.getenv("HOST");
        String user = System.getenv("USER");
        String password = System.getenv("PASSWORD");
        int queryInterval = Integer.parseInt(System.getenv("QUERY_INTERVAL"));

        Influx influx = new Influx(host, user, password);
        influx.createDatabase("NextCrawler");
        influx.enableBatchWrites();

        new Crawler(influx).start("fg", queryInterval);
    }

}
