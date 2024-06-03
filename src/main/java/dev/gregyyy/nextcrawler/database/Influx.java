package dev.gregyyy.nextcrawler.database;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;

import java.util.List;

public class Influx {

    private final InfluxDBClient client;
    private WriteApi writeApi;
    private QueryApi queryApi;

    public Influx(String url, String token, String org, String bucket) {
        this.client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        setApis();
    }

    public Influx(String url, String username, String password, String bucket, String org) {
        InfluxDBClientOptions options = InfluxDBClientOptions.builder()
                .url(url)
                .authenticate(username, password.toCharArray())
                .bucket(bucket)
                .org(org)
                .build();
        this.client = InfluxDBClientFactory.create(options);
        setApis();
    }

    private void setApis() {
        this.writeApi = client.makeWriteApi();
        this.queryApi = client.getQueryApi();
    }

    public void write(Point point) {
        writeApi.writePoint(point);
    }

    public void write(List<Point> points) {
        writeApi.writePoints(points);
    }

    public List<FluxTable> query(String query) {
        return queryApi.query(query);
    }

    public void dropMeasurement(String measurement) {
        queryApi.query("DROP MEASUREMENT " + measurement);
    }

}
