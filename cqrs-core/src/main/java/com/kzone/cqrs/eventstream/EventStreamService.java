package com.kzone.cqrs.eventstream;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static com.kzone.cqrs.eventstream.EventStream.EVENT_TYPE;

@RequiredArgsConstructor
@Service
public class EventStreamService {


    @Autowired
    private final EventStream eventStream;


    public void publish(Object event, String eventType) {
        eventStream.producer()
                .send(MessageBuilder.withPayload(event)
                        .setHeader(EVENT_TYPE, eventType)
                        .build());
    }

}
