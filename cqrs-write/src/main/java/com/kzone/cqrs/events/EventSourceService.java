package com.kzone.cqrs.events;

import java.util.List;

public interface EventSourceService {

    List<EventDTO> getEvents(String aggregateId);

    void save(Object event);

    boolean eventExists(String aggregateId, long version);

}
