package com.kzone.cqrs.events.entites;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class EventId implements Serializable {

    private String aggregate;
    private long version;

}
