package com.kzone.cqrs.util;

import com.kzone.cqrs.core.Version;
import com.kzone.cqrs.core.AggregateId;
import com.kzone.cqrs.listener.OnEvent;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;

@UtilityClass
@Log4j2
public class AggregateUtil {

    public static String getAggregateId(Object event) {
        var eventClass = event.getClass();
        var declaredFields = eventClass.getDeclaredFields();
        for (var declaredField : declaredFields) {
            if (declaredField.getAnnotation(AggregateId.class) == null) {
                continue;
            }

            try {
                return (String) readValue(event, declaredField.getName());
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("No aggregate id found");
    }


    public static String createGetter(String name) {
        if (name == null || name.length() == 0) {
            throw new RuntimeException("field name cannot be empty");
        }
        return "get" + name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    public static String createSetter(String name) {
        if (name == null || name.length() == 0) {
            throw new RuntimeException("field name cannot be empty");
        }
        return "set" + name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    private static Object readValue(Object event, String fieldName) throws InvocationTargetException, IllegalAccessException {

        var getter = createGetter(fieldName);
        var getterMethod = ReflectionUtils.findMethod(event.getClass(), getter);
        if (getterMethod == null) {
            throw new RuntimeException("Cannot find getter method for field " + fieldName);
        }
        return getterMethod.invoke(event, null);
    }

    private static Object writeValue(Object event, String fieldName, Object value) throws InvocationTargetException, IllegalAccessException {

        var getter = createSetter(fieldName);
        var getterMethod = ReflectionUtils.findMethod(event.getClass(), getter);
        if (getterMethod == null) {
            throw new RuntimeException("Cannot find setter method for field " + fieldName);
        }
        return getterMethod.invoke(event, value);
    }

    public static long getVersion(Object event) {

        var declaredFields = event.getClass().getDeclaredFields();
        for (var declaredField : declaredFields) {
            if (declaredField.getAnnotation(Version.class) == null) {
                continue;
            }

            try {

                return (long) readValue(event, declaredField.getName());
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("No version property found");
    }

    public static List<Method> getEventHandlingMethods(Object aggregate) {
        var declaredMethods = aggregate.getClass().getDeclaredMethods();
        log.debug("declared methods {}", declaredMethods);
        return Arrays.stream(declaredMethods).filter(method -> method.getAnnotation(OnEvent.class) != null)
                .collect(Collectors.toList());
    }

    public static Method getEventHandler(Object aggregate, Object event) {
        var methods = getEventHandlingMethods(aggregate);
        log.debug("Event handler methods {}", methods);
        List<Method> collect = methods.stream().filter(method -> {
            var parameters = method.getParameters();
            return parameters != null && parameters.length == 1;
        }).filter(method -> Arrays.stream(method.getParameterTypes())
                .anyMatch(aClass -> aClass == event.getClass())).collect(Collectors.toList());

        if (collect.size() != 1) {
            throw new RuntimeException("Multiple or no suitable handler found");
        }

        return collect.get(0);
    }


    public static void handleEvent(Object aggregateRoot, Object event) {
        var eventHandler = getEventHandler(aggregateRoot, event);
        log.info("Event handler {}", eventHandler);
        try {
            eventHandler.invoke(aggregateRoot, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] convertToByte(Object event) {
        var bo = new ByteArrayOutputStream();
        try (var so = new ObjectOutputStream(bo)) {
            so.writeObject(event);
            so.flush();
            return bo.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Object deSerialize(byte[] data) {
        try (var validatingObjectInputStream = new ValidatingObjectInputStream(new ByteArrayInputStream(data))) {
            validatingObjectInputStream.accept("com.*", "java.*");
            return validatingObjectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
