package com.github.ba1tan.crm_task.controller;

import com.github.ba1tan.crm_task.dto.ScheduleResponse;
import com.github.ba1tan.crm_task.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/managers")
public class ManagerController {
    private final ManagerService service;

    @GetMapping("/{id}/schedule")
    public ScheduleResponse getSchedule(@PathVariable Long id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
    {
        return service.schedule(id, date);
    }
}
