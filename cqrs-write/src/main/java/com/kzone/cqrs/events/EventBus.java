package com.kzone.cqrs.events;

public interface EventBus {

    void send(Object event);

}
