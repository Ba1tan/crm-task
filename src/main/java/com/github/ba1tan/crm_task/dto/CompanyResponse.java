package com.github.ba1tan.crm_task.dto;

import com.github.ba1tan.crm_task.entity.Company;
import com.github.ba1tan.crm_task.entity.CompanyStatus;

import java.time.Instant;

public record CompanyResponse(Long id, String name, String bin, String phone, CompanyStatus status, Instant createdAt) {
    public static CompanyResponse from(Company c){
        return new CompanyResponse(c.getId(), c.getName(), c.getBin(), c.getPhone(), c.getStatus(), c.getCreatedAt());
    }
}
