package com.github.ba1tan.crm_task.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum MeetingEventType {
    CREATED, RESCHEDULED, CANCELLED;

    @JsonValue
    public String json() {
        return name().toLowerCase(Locale.ROOT);
    }
}
