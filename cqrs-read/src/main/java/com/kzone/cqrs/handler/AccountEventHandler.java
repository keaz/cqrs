package com.kzone.cqrs.handler;

import com.kzone.cqrs.events.AccountCreated;
import com.kzone.cqrs.listener.EventHandler;
import com.kzone.cqrs.listener.OnEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@EventHandler
@Component
public class AccountEventHandler {

    @OnEvent
    public void handle(AccountCreated accountCreated) {
        log.info("Handled account created event {}", accountCreated);
    }

}
