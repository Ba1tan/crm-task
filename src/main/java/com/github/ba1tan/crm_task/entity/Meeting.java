package com.github.ba1tan.crm_task.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(name = "meetings")
@Getter
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    private Manager manager;

    private Instant startAt;

    private Instant endAt;

    private Instant initialStartAt;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status = MeetingStatus.SCHEDULED;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<MeetingEvent> events = new ArrayList<>();

    private String cancelReason;

    private Instant createdAt;

    private Instant updatedAt;

    public Meeting(Company company, Manager manager, Instant startAt, Instant endAt, Instant now){
        this.company = company;
        this.manager = manager;
        this.startAt = startAt;
        this.endAt = endAt;
        this.initialStartAt = startAt;
        this.createdAt = now;
        this.updatedAt = now;
        this.events.add(new MeetingEvent(this, MeetingEventType.CREATED, null, startAt, now));
    }

    public void reschedule(Instant newStart, Instant newEnd, Instant now){
        events.add(new MeetingEvent(this, MeetingEventType.RESCHEDULED, startAt, newStart, now));
        this.startAt = newStart;
        this.endAt = newEnd;
        this.updatedAt = now;
    }
    public void cancel(String reason, Instant now){
        events.add(new MeetingEvent(this, MeetingEventType.CANCELLED, startAt, null, now));
        this.status = MeetingStatus.CANCELLED;
        this.updatedAt = now;
        this.cancelReason = reason;
    }

}
