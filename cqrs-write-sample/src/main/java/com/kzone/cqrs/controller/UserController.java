package com.kzone.cqrs.controller;

import com.kzone.cqrs.aggregate.AccountAggregate;
import com.kzone.cqrs.command.CreateAccount;
import com.kzone.cqrs.command.DepositMoney;
import com.kzone.cqrs.events.aggregate.AggregateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/account")
public class UserController {

    private final AggregateRepository<AccountAggregate> aggregateRepository;

    @PostMapping
    public ResponseEntity<AccountAggregate> create(@RequestBody CreateAccount createAccount) {

        var apply = aggregateRepository.createAggregate(AccountAggregate.class)
                .handle(accountAggregate ->
                        accountAggregate.create(createAccount.getName(), createAccount.getBalance()))
                .apply();

        return new ResponseEntity<>(apply, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<AccountAggregate> deposit(@RequestBody DepositMoney depositMoney) {

        var accountAggregate = aggregateRepository.loadAggregate(depositMoney.getId(), AccountAggregate.class);
        var account = accountAggregate.handle(aggregate -> aggregate.deposited(depositMoney.getVersion(), depositMoney.getAmount()))
                .apply();
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

}
