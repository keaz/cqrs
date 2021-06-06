package com.kzone.cqrs.event;

import com.kzone.cqrs.core.Event;
import lombok.Value;

@Value
@Event(name = "money-withdrew")
public class MoneyWithdrawn {

    String aggregateId;
    long version;
    double amount;

}
