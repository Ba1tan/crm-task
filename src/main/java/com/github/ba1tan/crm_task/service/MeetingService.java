package com.github.ba1tan.crm_task.service;


import com.github.ba1tan.crm_task.dto.CreateMeetingRequest;
import com.github.ba1tan.crm_task.dto.MeetingResponse;
import com.github.ba1tan.crm_task.entity.*;
import com.github.ba1tan.crm_task.exception.ApiException;
import com.github.ba1tan.crm_task.repository.CompanyRepository;
import com.github.ba1tan.crm_task.repository.ManagerRepository;
import com.github.ba1tan.crm_task.repository.MeetingRepository;
import com.github.ba1tan.crm_task.utils.WorkingHoursConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private static final Long NO_ID = 0L;

    private final MeetingRepository meetingRepo;
    private final CompanyRepository compRepo;
    private final ManagerRepository managerRepo;
    private final Clock clock;

    @Transactional
    public MeetingResponse create(CreateMeetingRequest request)
    {
        return MeetingResponse.from(set(request.companyId(), request.managerId(), request.startAt()));
    }
    @Transactional
    public Meeting set(Long companyId, Long managerId, Instant startAt)
    {
        Company company = compRepo.findById(companyId)
                .orElseThrow(() -> ApiException.notFound("Компания не найдена"));
        Manager manager = managerRepo.findById(managerId)
                .orElseThrow(() -> ApiException.notFound("Менеджер не найден"));
        requireWorkingHours(startAt);
        Instant endAt = startAt.plus(WorkingHoursConstraint.getDuration());
        ensureFree(managerId, companyId, startAt, endAt, NO_ID);

        return meetingRepo.saveAndFlush(new Meeting(company, manager, startAt, endAt, clock.instant()));

    }

    @Transactional(readOnly = true)
    public MeetingResponse get(Long id)
    {
        return meetingRepo.findById(id).map(MeetingResponse::from).orElseThrow(() -> ApiException.notFound("Встреча не найдена"));
    }

    // вспомогательныее методы
    private void ensureFree(long managerId, long companyId, Instant start, Instant end, long excludeId) {
        if (meetingRepo.countManagerOverlaps(managerId, start, end, excludeId) > 0) {
            throw ApiException.conflict("MANAGER_BUSY", "У менеджера уже есть встреча в это время");
        }
        if (meetingRepo.countCompanyOverlaps(companyId, start, end, excludeId) > 0) {
            throw ApiException.conflict("COMPANY_BUSY", "У компаний уже есть встреча в это время");
        }
    }

    private void requireWorkingHours(Instant startAt) {
        if (!WorkingHoursConstraint.fits(startAt)) {
            throw ApiException.unprocessable("OUTSIDE_WORKING_HOURS",
                    "Встречи возможны только пн-пт с 09:00 до 18:00 (Asia/Almaty) и должен целиком уместиться в этот интервал");
        }
    }
}
