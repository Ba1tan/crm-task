package com.github.ba1tan.crm_task.repository;

import com.github.ba1tan.crm_task.entity.Company;
import com.github.ba1tan.crm_task.entity.CompanyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByBin(String bin);

    Page<Company> findByStatus(CompanyStatus status, Pageable pageable);

}