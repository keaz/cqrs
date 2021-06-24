package com.kzone.cqrs.events.entites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "event")
public class EventEntity {

    @EqualsAndHashCode.Include
    @EmbeddedId
    private EventId eventId;
    @Lob
    private byte[] event;

}
