package com.github.ba1tan.crm_task.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateMeetingRequest(@NotNull Long companyId, @NotNull Long managerId, @NotNull Instant startAt) {
}
