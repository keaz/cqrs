package com.kzone.cqrs.events;

import com.kzone.cqrs.events.entites.EventEntity;
import com.kzone.cqrs.events.entites.EventId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JPAEventRepository extends JpaRepository<EventEntity, EventId> {

    @Query("SELECT e FROM EventEntity e where e.eventId.aggregate = ?1 ORDER BY e.eventId.version")
    List<EventEntity> findByEventId(String aggregateId);

    @Query("SELECT new java.lang.Boolean(count(1) > 0) FROM EventEntity e where e.eventId.aggregate = ?1 and e.eventId.version = ?2")
    boolean eventExists(String aggregateId,long version);

}
