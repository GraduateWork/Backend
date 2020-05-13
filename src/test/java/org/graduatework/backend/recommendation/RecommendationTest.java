package org.graduatework.backend.recommendation;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.db.DBAdaptorInfo;
import org.graduatework.backend.dto.EventDto;
import org.graduatework.backend.services.EventService;
import org.graduatework.backend.utils.TestDBAdaptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RecommendationTest {

    private Configuration config;
    private EventService eventService;

    private void setup(String recManager, DBAdaptorInfo dbAdaptor) {
        config = new Configuration();
        config.setDataSourceUrl("");
        config.setJdbcUrl("");
        config.setRecommendationManager(recManager);
        eventService = new EventService(config, dbAdaptor);
    }

    @Test
    public void testUserBasedRecommendation() {
        TestDBAdaptor dbAdaptor = new TestDBAdaptor();
        // TODO: Fill data.
        setup("UserBasedRecommendationManager", dbAdaptor);
        String username = "";
        List<EventDto> events = eventService.getEvents(username, null);
        Assertions.assertNotNull(events);
    }
}
