package com.kzone.cqrs.eventstream;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventStreamService {

    private static final String EVENT_TYPE = "x-event-type";

    @Autowired
    private final EventStream eventStream;


    public void publish(Object accountCreated, String eventType) {
        eventStream.producer()
                .send(MessageBuilder.withPayload(accountCreated)
                        .setHeader(EVENT_TYPE, eventType)
                        .build());
    }

}
