package com.github.ba1tan.crm_task.service;

import com.github.ba1tan.crm_task.dto.CompanyResponse;
import com.github.ba1tan.crm_task.dto.CreateCompanyRequest;
import com.github.ba1tan.crm_task.dto.PageResponse;
import com.github.ba1tan.crm_task.entity.Company;
import com.github.ba1tan.crm_task.entity.CompanyStatus;
import com.github.ba1tan.crm_task.exception.ApiException;
import com.github.ba1tan.crm_task.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository compRepo;

    public CompanyResponse create(CreateCompanyRequest request)
    {
        if(compRepo.existsByBin(request.bin()))
        {
            throw ApiException.conflict("BIN_ALREADY_EXISTS", "Компания с таким БИН уже сущечтвет");
        }
        Company saved = compRepo.saveAndFlush(new Company(request.name(), request.bin(), request.phone(), Instant.now()));
        return CompanyResponse.from(saved);
    }
    public PageResponse<CompanyResponse> list(CompanyStatus status, int page, int size)
    {
        if(page < 0 || size < 1 || size > 100)
        {
            throw ApiException.badRequest("page >= 0, size в диапазоне 1..100");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        var result = status == null ? compRepo.findAll(pageable) : compRepo.findByStatus(status, pageable);
        return PageResponse.of(result.map(CompanyResponse::from));
    }

}
