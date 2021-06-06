package com.kzone.cqrs.events;

import com.kzone.cqrs.core.Aggregate;
import com.kzone.cqrs.core.AggregateId;
import com.kzone.cqrs.events.entites.EventEntity;
import com.kzone.cqrs.util.AggregateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

import static com.kzone.cqrs.util.AggregateUtil.deSerialize;
import static com.kzone.cqrs.util.AggregateUtil.handleEvent;

@RequiredArgsConstructor
@Log4j2
@Service
public class AggregateRepositoryImpl<T> implements AggregateRepository<T> {

    private final EventRepository eventRepository;
    private final EventBus eventBus;

    @Override
    public Aggregate<T> loadAggregate(String aggregateId, Class<T> aggregateType) {
        List<EventEntity> events = eventRepository.findByEventId(aggregateId);
        T aggregate = createNewAggregate(aggregateType);
        setAggregateId(aggregate,aggregateType,aggregateId);
        events.forEach(eventEntity -> handleEvent(aggregate,deSerialize(eventEntity.getEvent())));
        return new Aggregate<>(aggregate,eventRepository,eventBus);
    }

    @Override
    public Aggregate<T> createAggregate(Class<T> aggregateType) {
        var aggregate = createNewAggregate(aggregateType);
        setAggregateId(aggregate, aggregateType,UUID.randomUUID().toString());
        return new Aggregate<>(aggregate,eventRepository,eventBus);
    }

    public T createNewAggregate(Class<T> aggregateType){
        try {
            return aggregateType.getDeclaredConstructor(null).newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void setAggregateId(T aggregate, Class<T> aggregateType, String value) {
        Field[] declaredFields = aggregateType.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getAnnotation(AggregateId.class) == null) {
                continue;
            }

            PropertyDescriptor descriptor = null;
            try {
                descriptor = new PropertyDescriptor(declaredField.getName(), aggregateType);
                descriptor.getWriteMethod().invoke(aggregate, value);
            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
