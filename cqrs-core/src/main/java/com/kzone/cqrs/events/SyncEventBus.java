package com.kzone.cqrs.events;

import com.kzone.cqrs.core.Event;
import com.kzone.cqrs.eventstream.EventStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class SyncEventBus implements EventBus {

    private final EventStreamService eventStreamService;

    @Override
    public void send(Object event) {
        eventStreamService.publish(event,eventName(event));
    }

    public String eventName(Object event){
        Event annotation = event.getClass().getAnnotation(Event.class);
        if (annotation == null) {
            throw new RuntimeException("Not an event!!!");
        }
        return annotation.name();
    }


}
