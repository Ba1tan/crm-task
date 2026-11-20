package com.github.ba1tan.crm_task.controller;

import com.github.ba1tan.crm_task.dto.CreateMeetingRequest;
import com.github.ba1tan.crm_task.dto.MeetingResponse;
import com.github.ba1tan.crm_task.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.github.ba1tan.crm_task.dto.UpdateMeetingRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("meetings")
public class MeetingController {
    private final MeetingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingResponse create(@Valid @RequestBody CreateMeetingRequest request)
    {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public MeetingResponse get(@PathVariable Long id)
    {
        return service.get(id);
    }

    @PatchMapping("/{id}")
    public MeetingResponse update(@PathVariable Long id, @Valid @RequestBody UpdateMeetingRequest request)
    {
        return service.update(id, request);
    }
}
