package com.kzone.cqrs.events.aggregate;

import org.springframework.lang.NonNull;

public interface AggregateRepository<T> {

    Aggregate<T> loadAggregate(@NonNull String aggregateId, Class<T> aggregateType);

    Aggregate<T> createAggregate(Class<T> aggregateType);

}
