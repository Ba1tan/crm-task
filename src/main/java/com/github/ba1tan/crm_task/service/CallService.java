package com.github.ba1tan.crm_task.service;

import com.github.ba1tan.crm_task.dto.CallResponse;
import com.github.ba1tan.crm_task.dto.CreateCallRequest;
import com.github.ba1tan.crm_task.entity.CallResult;
import com.github.ba1tan.crm_task.entity.Calls;
import com.github.ba1tan.crm_task.entity.Company;
import com.github.ba1tan.crm_task.entity.Meeting;
import com.github.ba1tan.crm_task.exception.ApiException;
import com.github.ba1tan.crm_task.repository.CallRepository;
import com.github.ba1tan.crm_task.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class CallService {
    private final CallRepository callRepo;
    private final MeetingService meetingService;
    private final CompanyRepository companyRepo;
    private final Clock clock;

    @Transactional
    public CallResponse create(CreateCallRequest request)
    {
        Company company = companyRepo.findById(request.companyId()).orElseThrow(() -> ApiException.notFound("Компания не найдена"));
        Meeting meeting = null;
        if(request.result() == CallResult.MEETING_SET)
        {
            if(request.meeting() == null)
            {
                throw ApiException.unprocessable("MEETING_REQUIRED", "Для результата meeting-set нужен блок meeting {managerId, startAt}");
            }
            meeting = meetingService.set(company.getId(), request.meeting().managerId(), request.meeting().startAt());

        }
        else if (request.meeting() != null)
        {
            throw ApiException.unprocessable("MEETING_NOT_ALLOWED",
                    "Блок meeting допустим только для результата meeting-set");
        }
        company.setStatus(request.result().toCompanyStatus());
        Calls saved = callRepo.saveAndFlush(new Calls(company, request.result(), meeting, request.comment(), clock.instant()));
        return CallResponse.from(saved);

    }
}
