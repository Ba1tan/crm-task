package com.github.ba1tan.crm_task.controller;

import com.github.ba1tan.crm_task.dto.CallResponse;
import com.github.ba1tan.crm_task.dto.CreateCallRequest;
import com.github.ba1tan.crm_task.service.CallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calls")
public class CallController {
    private final CallService service;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CallResponse create(@Valid @RequestBody CreateCallRequest request)
    {
        return service.create(request);
    }
}
