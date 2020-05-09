package org.graduatework.backend.recommendation;

import org.graduatework.backend.dto.EventDto;

import java.util.List;

public interface RecommendationManager {
    List<EventDto> sortByPreference(List<EventDto> events, String username);
}
