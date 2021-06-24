package com.kzone.cqrs.events.aggregate;

import com.kzone.cqrs.core.AggregateId;
import com.kzone.cqrs.events.EventBus;
import com.kzone.cqrs.events.EventDTO;
import com.kzone.cqrs.events.EventSourceService;
import com.kzone.cqrs.util.AggregateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
@Service
public class AggregateRepositoryImpl<T> implements AggregateRepository<T> {

    private final EventSourceService eventSourceService;
    private final EventBus eventBus;

    @Override
    public Aggregate<T> loadAggregate(String aggregateId, Class<T> aggregateType) {
        List<EventDTO> events = eventSourceService.getEvents(aggregateId);
        var aggregate = createNewAggregate(aggregateType);
        setAggregateId(aggregate, aggregateType, aggregateId);
        events.forEach(eventDTO -> AggregateUtil.handleEvent(aggregate, eventDTO.getEvent()));
        return new Aggregate<>(aggregate, eventSourceService, eventBus);
    }

    @Override
    public Aggregate<T> createAggregate(Class<T> aggregateType) {
        var aggregate = createNewAggregate(aggregateType);
        setAggregateId(aggregate, aggregateType, UUID.randomUUID().toString());
        return new Aggregate<>(aggregate, eventSourceService, eventBus);
    }

    public T createNewAggregate(Class<T> aggregateType) {
        try {
            return aggregateType.getDeclaredConstructor(null).newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void setAggregateId(T aggregate, Class<T> aggregateType, String value) {
        var declaredFields = aggregateType.getDeclaredFields();
        for (var declaredField : declaredFields) {
            if (declaredField.getAnnotation(AggregateId.class) == null) {
                continue;
            }

            try {
                var descriptor = new PropertyDescriptor(declaredField.getName(), aggregateType);
                descriptor.getWriteMethod().invoke(aggregate, value);
            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
