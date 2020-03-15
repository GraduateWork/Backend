package org.graduatework.backend;

import org.graduatework.backend.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class BackendApplication {

    private static final long DAY = 24 * 60 * 60 * 1000L;

    private static DataService dataService;

    private static void requestData() {
        try {
            dataService.requestData();
        } catch (Throwable e) {
            System.err.println("Failed to request a data:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                requestData();
            }
        }, 0, DAY);
    }

    @Autowired
    public void setDataService(DataService dataService) {
        BackendApplication.dataService = dataService;
    }
}
