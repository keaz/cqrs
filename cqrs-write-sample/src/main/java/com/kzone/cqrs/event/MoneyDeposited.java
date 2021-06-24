package com.kzone.cqrs.event;

import com.kzone.cqrs.core.AggregateId;
import com.kzone.cqrs.core.Version;
import com.kzone.cqrs.core.Event;
import lombok.Value;

import java.io.Serializable;

@Value
@Event(name = "money-deposited")
public class MoneyDeposited implements Serializable {

    @AggregateId
    String aggregateId;
    @Version
    long version;
    double amount;

}
