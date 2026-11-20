package com.github.ba1tan.crm_task.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "calls")
@NoArgsConstructor
@Setter
@Getter
public class Calls {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    private CallResult result;

    @ManyToOne(fetch = FetchType.LAZY)
    private Meeting meeting;

    private String comment;

    private Instant calledAt;

    public Calls(Company company, CallResult result, Meeting meeting, String comment, Instant calledAt)
    {
        this.company = company;
        this.result = result;
        this.meeting = meeting;
        this.comment = comment;
        this.calledAt = calledAt;
    }
}
