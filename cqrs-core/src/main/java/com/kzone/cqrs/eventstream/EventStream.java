package com.kzone.cqrs.eventstream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface EventStream {

    String EVENT_TYPE = "x-event-type";

    String OUTBOUND = "event-producer";
    String INBOUND = "event-consumer";

    @Output(OUTBOUND)
    MessageChannel producer();

    @Input(INBOUND)
    SubscribableChannel inputChannel();



}
