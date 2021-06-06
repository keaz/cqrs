package com.kzone.cqrs.events;

import com.kzone.cqrs.core.Aggregate;
import org.springframework.lang.NonNull;

public interface AggregateRepository<T> {

    Aggregate<T> loadAggregate(@NonNull String aggregateId, Class<T> aggregateType);

    Aggregate<T> createAggregate(Class<T> aggregateType);

}
