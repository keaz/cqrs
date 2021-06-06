package com.kzone.cqrs.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositMoney {

    private String id;
    private double amount;
    private long version;

}
