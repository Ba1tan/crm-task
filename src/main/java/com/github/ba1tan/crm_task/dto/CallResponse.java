package com.github.ba1tan.crm_task.dto;

import com.github.ba1tan.crm_task.entity.CallResult;
import com.github.ba1tan.crm_task.entity.Calls;

import java.time.Instant;

public record CallResponse(Long id, Long companyId, CallResult result, Long meetingId, String comment, Instant calledAt) {
    public static CallResponse from(Calls c)
    {
        return new CallResponse(c.getId(), c.getCompany().getId(), c.getResult(), c.getMeeting().getId(), c.getComment(), c.getCalledAt());

    }
}
