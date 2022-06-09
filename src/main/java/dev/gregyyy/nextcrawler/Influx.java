package dev.gregyyy.nextcrawler;


import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

public class Influx {

    private final InfluxDB client;

    public Influx(String host, String user, String password) {
        this.client = InfluxDBFactory.connect(host, user, password);
    }

    public void createDatabase(String name) {
        client.query(new Query("CREATE DATABASE " + name));
        client.setDatabase(name);
    }

    public void enableBatchWrites() {
        client.enableBatch(BatchOptions.DEFAULTS.threadFactory(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }));
    }

    public void write(Point point) {
        client.write(point);
    }

    public void write(BatchPoints points) {
        client.write(points);
    }

    public QueryResult query(String query) {
        return client.query(new Query(query));
    }

    public void dropMeasurement(String measurement) {
        client.query(new Query("DROP MEASUREMENT " + measurement));
    }

}
