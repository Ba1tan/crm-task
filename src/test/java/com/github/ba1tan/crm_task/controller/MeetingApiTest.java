package com.github.ba1tan.crm_task.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


 //Интеграционные тесты

 //Граница рабочего дня  проверяется в WorkingHoursPolicyTest.
@SpringBootTest
@Testcontainers
class MeetingApiTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

    // Среда 2026-11-25, Almaty = UTC+5. Менеджеры 1 и 2 создаются seed-миграцией V2.
    static final String T_10_00 = "2026-11-25T05:00:00Z";
    static final String T_10_30 = "2026-11-25T05:30:00Z";
    static final String T_11_00 = "2026-11-25T06:00:00Z";
    static final AtomicLong SEQ = new AtomicLong();

    @Autowired WebApplicationContext ctx;
    @Autowired JdbcTemplate jdbc;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx).build();
        jdbc.execute("TRUNCATE calls, meeting_events, meetings, companies RESTART IDENTITY CASCADE");
    }

    //  у менеджера нет пересекающихся встреч, встык - можно
    @Test
    void overlappingMeetingsOfSameManager_areRejected_adjacentAreAllowed() throws Exception {
        long a = newCompany(), b = newCompany(), c = newCompany();
        book(a, 1, T_10_00).andExpect(status().isCreated());

        book(b, 1, T_10_30)                                      // 10:30 попадает внутрь 10:00-11:00
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("MANAGER_BUSY"));

        book(c, 1, T_11_00)                                      // 11:00-12:00 встык: не конфликт
                .andExpect(status().isCreated());
    }

    //  у одной организации нет двух активных встреч одновременно, даже с разными менеджерами
    @Test
    void sameCompanyWithTwoManagersAtSameTime_isRejected() throws Exception {
        long a = newCompany();
        book(a, 1, T_10_00).andExpect(status().isCreated());

        book(a, 2, T_10_00)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("COMPANY_BUSY"));
    }

    // звонок meeting_set без созданной встречи - ошибка валидации
    @Test
    void meetingSetCallWithoutMeeting_isValidationError() throws Exception {
        long a = newCompany();
        mvc.perform(post("/calls").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyId\":%d,\"result\":\"meeting_set\"}".formatted(a)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("MEETING_REQUIRED"));
    }

    // перенос не теряет историю, видно когда встреча была назначена изначально
    @Test
    void reschedule_keepsInitialStartAndWritesHistory() throws Exception {
        long a = newCompany();
        long id = idOf(book(a, 1, T_10_00).andExpect(status().isCreated()));

        mvc.perform(patch("/meetings/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startAt\":\"%s\"}".formatted(T_11_00)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startAt").value(T_11_00))
                .andExpect(jsonPath("$.initialStartAt").value(T_10_00))
                .andExpect(jsonPath("$.history.length()").value(2))
                .andExpect(jsonPath("$.history[0].type").value("created"))
                .andExpect(jsonPath("$.history[1].type").value("rescheduled"))
                .andExpect(jsonPath("$.history[1].oldStartAt").value(T_10_00));
    }


    private long newCompany() throws Exception {
        long n = SEQ.incrementAndGet();
        String body = """
                {"name":"Company %d","bin":"%012d","phone":"+77010000000"}""".formatted(n, n);
        return idOf(mvc.perform(post("/companies").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()));
    }

    private ResultActions book(long companyId, long managerId, String startAt) throws Exception {
        return mvc.perform(post("/meetings").contentType(MediaType.APPLICATION_JSON).content("""
                {"companyId":%d,"managerId":%d,"startAt":"%s"}""".formatted(companyId, managerId, startAt)));
    }

    private long idOf(ResultActions actions) throws Exception {
        Number id = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }
}