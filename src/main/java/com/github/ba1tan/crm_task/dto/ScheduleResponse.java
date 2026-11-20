package com.github.ba1tan.crm_task.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ScheduleResponse(Long managerId, LocalDate date, String zone, List<Item> meetings) {
    public record Item(Long id, Long companyId, String companyName, Instant startAt, Instant endAt, Instant initialStartAt){}
}
