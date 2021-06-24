package com.kzone.cqrs.events.aggregate;

@FunctionalInterface
public interface AggregateUnitOfWork<T> {

    T apply();

}
