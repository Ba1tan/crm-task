package com.github.ba1tan.crm_task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Table(name = "meeting_events")
@Getter
public class MeetingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    private MeetingEventType type;

    private Instant oldStartAt;

    private Instant newStartAt;

    private Instant occurredAt;

    public MeetingEvent(Meeting meeting, MeetingEventType type, Instant oldStartAt, Instant newStartAt, Instant occurredAt){
        this.meeting = meeting;
        this.type = type;
        this.newStartAt = newStartAt;
        this.oldStartAt = oldStartAt;
        this.occurredAt = occurredAt;
    }
}
