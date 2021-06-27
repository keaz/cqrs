package com.kzone.cqrs.events;

import com.kzone.cqrs.core.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SyncEventBus implements EventBus {

    private final EventStreamService eventStreamService;

    @Override
    public void send(Object event) {
        eventStreamService.publish(event, eventName(event));
    }

    public String eventName(Object event) {
        var annotation = event.getClass().getAnnotation(Event.class);
        if (annotation == null) {
            throw new RuntimeException("Not an event!!!");
        }
        return annotation.name();
    }


}
