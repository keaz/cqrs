package com.kzone.cqrs.events.service;

import com.kzone.cqrs.events.EventDTO;
import com.kzone.cqrs.events.EventSourceService;
import com.kzone.cqrs.util.AggregateUtil;
import com.kzone.cqrs.events.JPAEventRepository;
import com.kzone.cqrs.events.entites.EventEntity;
import com.kzone.cqrs.events.entites.EventId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.kzone.cqrs.util.AggregateUtil.convertToByte;

@RequiredArgsConstructor
@Service
public class JPAEventSourceService implements EventSourceService {

    private final JPAEventRepository eventRepository;

    @Override
    public List<EventDTO> getEvents(String aggregateId) {
        var events = eventRepository.findByEventId(aggregateId);
        return events.stream().map(eventEntity -> new EventDTO(eventEntity.getEventId().getAggregate(),
                eventEntity.getEventId().getVersion(), AggregateUtil.deSerialize(eventEntity.getEvent())))
                .collect(Collectors.toList());

    }

    @Override
    public void save(Object event) {
        var aggregateId = AggregateUtil.getAggregateId(event);
        long version = AggregateUtil.getVersion(event);

        var eventId = new EventId();
        eventId.setAggregate(aggregateId);
        eventId.setVersion(version);
        var eventEntity = new EventEntity();
        eventEntity.setEventId(eventId);

        var bytes = convertToByte(event);
        eventEntity.setEvent(bytes);
        eventRepository.save(eventEntity);
    }

    @Override
    public boolean eventExists(String aggregateId, long version) {
        return eventRepository.eventExists(aggregateId,version);
    }

}
