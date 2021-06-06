package com.kzone.cqrs.aggregate;

import com.kzone.cqrs.core.AggregateId;
import com.kzone.cqrs.core.AggregateRoot;
import com.kzone.cqrs.event.AccountCreated;
import com.kzone.cqrs.event.MoneyDeposited;
import com.kzone.cqrs.event.MoneyWithdrawn;
import com.kzone.cqrs.listener.OnEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AggregateRoot
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountAggregate {

    @AggregateId
    private String aggregateId;
    private String name;
    private Double balance;

    @OnEvent
    public void on(AccountCreated created) {
        name = created.getName();
        balance = created.getAmount();
    }

    @OnEvent
    public void on(MoneyDeposited moneyDeposited) {
        balance = balance + moneyDeposited.getAmount();
    }

    @OnEvent
    public void on(MoneyWithdrawn moneyDeposited) {
        balance = balance - moneyDeposited.getAmount();
    }

    public AccountCreated create(String name, double balance) {
        return new AccountCreated(UUID.randomUUID().toString(), 1, name, balance);
    }

    public MoneyDeposited deposited(long version, double amount) {
        return new MoneyDeposited(aggregateId, version, amount);
    }

    public MoneyWithdrawn withdraw(long version, double amount) {
        if (balance >= amount) {
            return new MoneyWithdrawn(aggregateId, version, amount);
        }
        throw new RuntimeException("");
    }

}
