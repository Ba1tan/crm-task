package com.github.ba1tan.crm_task.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String bin;

    private String phone;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status = CompanyStatus.NEW;

    private Instant createdAt;

    public Company(String name, String bin, String phone, Instant createdAt)
    {
        this.name = name;
        this.bin = bin;
        this.phone = phone;
        this.createdAt = createdAt;
    }

}
