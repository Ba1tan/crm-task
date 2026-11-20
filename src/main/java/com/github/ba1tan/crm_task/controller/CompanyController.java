package com.github.ba1tan.crm_task.controller;

import com.github.ba1tan.crm_task.dto.CompanyResponse;
import com.github.ba1tan.crm_task.dto.CreateCompanyRequest;
import com.github.ba1tan.crm_task.dto.PageResponse;
import com.github.ba1tan.crm_task.entity.CompanyStatus;
import com.github.ba1tan.crm_task.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse create(@Valid @RequestBody CreateCompanyRequest request)
    {
        return service.create(request);
    }
    @GetMapping
    public PageResponse<CompanyResponse> list(@RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size)
    {
        return service.list(status == null ? null : CompanyStatus.parse(status), page, size);
    }
}
