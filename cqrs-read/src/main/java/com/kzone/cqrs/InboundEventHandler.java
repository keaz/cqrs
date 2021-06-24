package com.kzone.cqrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kzone.cqrs.core.Event;
import com.kzone.cqrs.eventstream.EventStream;
import com.kzone.cqrs.listener.EventHandler;
import com.kzone.cqrs.listener.OnEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
public class InboundEventHandler {

    public final Map<String, EventHandlerWrapper> handlerMap;
    private final ObjectMapper objectMapper;

    public InboundEventHandler(ConfigurableApplicationContext applicationContext,ObjectMapper objectMapper) {
        var beansWithAnnotation = applicationContext.getBeanFactory().getBeansWithAnnotation(EventHandler.class);
        handlerMap = beansWithAnnotation.values().stream()
                .map(this::getEventHandler).flatMap(Collection::stream)
                .collect(Collectors.toMap(EventHandlerWrapper::getEventType, Function.identity()));
        this.objectMapper = objectMapper;

    }


    @StreamListener(EventStream.INBOUND)
    public void getMessage(Message<?> message) {
        var headers = message.getHeaders();
        var eventType = (String) headers.get(EventStream.EVENT_TYPE);
        if (eventType == null || eventType.isEmpty()) {
            log.error("Invalid event, " + EventStream.EVENT_TYPE + " is null");
            throw new RuntimeException("Invalid event, " + EventStream.EVENT_TYPE + " is null");
        }
        var payload = (String) message.getPayload();
        log.info("Event received {}", payload);
        handleEvent(payload,eventType);
    }

    private void handleEvent(String payload,String eventType){
        var eventHandlerWrapper = handlerMap.get(eventType);
        try {
            var event = objectMapper.readValue(payload, eventHandlerWrapper.eventClass);
            eventHandlerWrapper.handleEvent(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Method> getEventHandlingMethods(Object aggregate) {
        var declaredMethods = aggregate.getClass().getDeclaredMethods();
        log.debug("declared methods {} of event handler {}", declaredMethods, aggregate);
        return Arrays.stream(declaredMethods).filter(method -> method.getAnnotation(OnEvent.class) != null)
                .collect(Collectors.toList());
    }


    private List<EventHandlerWrapper> getEventHandler(Object eventHandler) {
        var methods = getEventHandlingMethods(eventHandler);
        log.debug("Event handler methods {}", methods);
        var eventHandlerWrappers = methods.stream().filter(method -> {
            var parameters = method.getParameters();
            return parameters != null && parameters.length == 1;
        }).filter(method ->
                method.getParameterTypes()[0].getAnnotation(Event.class) != null
        ).map(method -> {
            var eventType = method.getParameterTypes()[0];
            var annotation = eventType.getAnnotation(Event.class);
            return new EventHandlerWrapper(annotation.name(), eventHandler, method,eventType);
        }).collect(Collectors.toList());

        if (eventHandlerWrappers.size() != 1) {
            throw new RuntimeException("Multiple or no suitable handler found");
        }

        return eventHandlerWrappers;
    }

    @Value
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class EventHandlerWrapper {

        @EqualsAndHashCode.Include
        String eventType;
        Object handler;
        Method method;
        Class<?> eventClass;

        public void handleEvent(Object event) {
            try {
                method.invoke(handler, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }


}
