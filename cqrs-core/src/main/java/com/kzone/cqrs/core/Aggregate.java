package com.kzone.cqrs.core;

import com.kzone.cqrs.events.AggregateUnitOfWork;
import com.kzone.cqrs.events.EventBus;
import com.kzone.cqrs.events.EventRepository;
import com.kzone.cqrs.events.entites.EventEntity;
import com.kzone.cqrs.events.entites.EventId;
import com.kzone.cqrs.util.AggregateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

import static com.kzone.cqrs.util.AggregateUtil.convertToByte;
import static com.kzone.cqrs.util.AggregateUtil.handleEvent;

@Log4j2
@RequiredArgsConstructor
public class Aggregate<T> {

    private final T aggregateRoot;
    private final EventRepository eventRepository;
    private final EventBus eventBus;

    public AggregateUnitOfWork<T> handle(Function<T, Object> tObjectFunction) {
        return () -> {
            var event = tObjectFunction.apply(aggregateRoot);
            String aggregateId = AggregateUtil.getAggregateId(event);
            long version = AggregateUtil.getVersion(event);
            isEvent(event);
            isEventExists(aggregateId,version);
            save(event);
            handleEvent(aggregateRoot, event);
            eventBus.send(event);
            return aggregateRoot;
        };
    }

    public T getAggregateRoot() {
        return aggregateRoot;
    }

    private void isEvent(Object event) {
        if (event.getClass().getAnnotation(Event.class) == null) {
            throw new RuntimeException("Not an event!!!");
        }
    }

    private void save(Object event) {
        String aggregateId = AggregateUtil.getAggregateId(event);
        long version = AggregateUtil.getVersion(event);

        var eventId = new EventId();
        eventId.setAggregate(aggregateId);
        eventId.setVersion(version);
        var eventEntity = new EventEntity();
        eventEntity.setEventId(eventId);

        byte[] bytes = convertToByte(event);
        eventEntity.setEvent(bytes);
        eventRepository.save(eventEntity);
    }

    private void isEventExists(String aggregateId, long version) {
        if (eventRepository.eventExists(aggregateId, version)) {
            throw new RuntimeException("Event exists with the same aggregate " + aggregateId + " and version " + version);
        }
    }

}
