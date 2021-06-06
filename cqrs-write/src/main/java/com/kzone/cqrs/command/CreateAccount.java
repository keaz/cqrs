package com.kzone.cqrs.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccount {

    private String name;
    private double balance;

}
