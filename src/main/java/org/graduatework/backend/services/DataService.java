package org.graduatework.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.dto.Event;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

@Service
public class DataService extends BaseService {

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 60 * 60 * 1000;
    private static final int PING_PERIODICITY = 60 * 1000;
    private static final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    private static final JSONParser jsonParser = new JSONParser();

    private static final String DATA = "/tutby/films";

    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    public DataService(Configuration config) {
        super(config);
    }

    public void requestData() throws IOException, InterruptedException, ParseException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.getDataSourceUrl() + DATA)).GET().build();
        HttpResponse<String> response;
        do {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            System.out.println("Ping data scraper: " + response.statusCode());
            if (response.statusCode() != 200)
                Thread.sleep(PING_PERIODICITY);
        } while (response.statusCode() != 200);
        parseData(response.body());
//        Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(new File("output.txt"))));
//        parseData(sc.nextLine());
        /*PrintWriter writer = new PrintWriter(new File("output.txt"));
        writer.println(response.body());
        writer.flush();
        writer.close();*/
    }

    @SuppressWarnings("unchecked")
    private void parseData(String data) throws IOException, ParseException {
        List<Event> events = mapper.readValue(data, new TypeReference<List<Event>>() {
        });
        JSONArray jsonArray = (JSONArray) jsonParser.parse(data);
        int eventNum = 0;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            for (String key : (Iterable<String>) jsonObject.keySet()) {
                if (!Event.jsonFieldNames.contains(key)) {
                    events.get(eventNum).getDetails().put(key, (String) jsonObject.get(key));
                }
            }
            eventNum++;
        }
        writeDataToDB(events);
    }

    private void writeDataToDB(List<Event> events) {
        dbAdaptor.clearEvents();
        dbAdaptor.insertEvents(events);
    }
}
