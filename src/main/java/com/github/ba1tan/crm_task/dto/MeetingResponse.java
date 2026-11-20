package com.github.ba1tan.crm_task.dto;

import com.github.ba1tan.crm_task.entity.Meeting;
import com.github.ba1tan.crm_task.entity.MeetingEventType;
import com.github.ba1tan.crm_task.entity.MeetingStatus;

import java.time.Instant;
import java.util.List;

public record MeetingResponse(Long id, Long companyId, Long managerId, Instant startAt, Instant endAt, Instant initialStartAt, MeetingStatus status,
                              String cancelReason, Instant createdAt, Instant updatedAt, List<Event> history) {

    public record Event(MeetingEventType type, Instant oldStartAt, Instant newStartAt, Instant occurredAt){}

    public static MeetingResponse from(Meeting m)
    {
        List<Event> history = m.getEvents().stream().map(e -> new Event(e.getType(), e.getOldStartAt(), e.getNewStartAt(), e.getOccurredAt()))
                .toList();
        return new MeetingResponse(m.getId(), m.getCompany().getId(), m.getManager().getId(), m.getStartAt(), m.getEndAt(), m.getInitialStartAt(), m.getStatus(), m.getCancelReason(), m.getCreatedAt(), m.getUpdatedAt(), history);
    }

}
