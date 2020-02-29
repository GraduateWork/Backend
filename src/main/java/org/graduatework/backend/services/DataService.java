package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class DataService extends BaseService {

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 60 * 60 * 1000;
    private static final String DATA = "/data";

    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    public DataService(Configuration config) {
        super(config);
    }

    public void requestData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.getDataSourceUrl() + DATA)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        // TODO: write to db.
    }
}
