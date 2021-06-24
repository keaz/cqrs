package com.kzone.cqrs.events;

import lombok.Value;

@Value
public class EventDTO {

    private String aggregate;
    private long version;
    private Object event;


}
