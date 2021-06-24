package com.kzone.cqrs.events.aggregate;


import com.kzone.cqrs.core.Event;
import com.kzone.cqrs.events.EventBus;
import com.kzone.cqrs.events.EventSourceService;
import com.kzone.cqrs.util.AggregateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

import static com.kzone.cqrs.util.AggregateUtil.handleEvent;

@Log4j2
@RequiredArgsConstructor
public class Aggregate<T> {

    private final T aggregateRoot;
    private final EventSourceService eventSourceService;
    private final EventBus eventBus;

    public AggregateUnitOfWork<T> handle(Function<T, Object> tObjectFunction) {
        return () -> {
            var event = tObjectFunction.apply(aggregateRoot);
            var aggregateId = AggregateUtil.getAggregateId(event);
            var version = AggregateUtil.getVersion(event);
            isEvent(event);
            isEventExists(aggregateId, version);
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
        eventSourceService.save(event);
    }

    private void isEventExists(String aggregateId, long version) {
        if (eventSourceService.eventExists(aggregateId, version)) {
            throw new RuntimeException("Event exists with the same aggregate " + aggregateId + " and version " + version);
        }
    }

}
