package com.kzone.cqrs.events;

@FunctionalInterface
public interface AggregateUnitOfWork<T> {

    T apply();

}
