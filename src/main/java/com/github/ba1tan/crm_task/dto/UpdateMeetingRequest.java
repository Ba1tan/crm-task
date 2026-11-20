package com.github.ba1tan.crm_task.dto;

import com.github.ba1tan.crm_task.entity.MeetingStatus;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateMeetingRequest(Instant startAt, MeetingStatus status, @Size(max = 500) String reason) {

}
