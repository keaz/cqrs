package com.kzone.cqrs;

import com.kzone.cqrs.eventstream.EventStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;


@SpringBootApplication
@EnableBinding(EventStream.class)
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
