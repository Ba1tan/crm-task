package com.github.ba1tan.crm_task.dto;

import com.github.ba1tan.crm_task.entity.CallResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateCallRequest(@NotNull Long companyId, @NotNull CallResult result, @Valid Slot meeting, @Size(max = 500) String comment) {
    public record Slot(@NotNull Long managerId, @NotNull Instant startAt){}
}
