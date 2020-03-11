package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class DataService extends BaseService {

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 60 * 60 * 1000;
    private static final int PING_PERIODICITY = 5000;
    private static final String DATA = "/tutby/films";

    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    public DataService(Configuration config) {
        super(config);
    }

    public void requestData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.getDataSourceUrl() + DATA)).GET().build();
        HttpResponse<String> response;
        do {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            System.out.println("Ping data scraper: " + response.statusCode());
            Thread.sleep(PING_PERIODICITY);
        } while (response.statusCode() != 200);
        PrintWriter writer = new PrintWriter(new File("output.txt"));
        writer.println(response.body());
    }
}
