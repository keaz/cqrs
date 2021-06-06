package com.kzone.cqrs.event;

import com.kzone.cqrs.core.AggregateId;
import com.kzone.cqrs.core.Event;
import com.kzone.cqrs.core.Version;
import lombok.Value;

import java.io.Serializable;

@Value
@Event(name = "account-created")
public class AccountCreated implements Serializable {

    @AggregateId
    String aggregateId;
    @Version
    long version;
    String name;
    double amount;

}
