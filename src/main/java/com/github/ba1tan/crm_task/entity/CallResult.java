package com.github.ba1tan.crm_task.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum CallResult {
    NO_ANSWER,
    REFUSED,
    MEETING_SET;
    @JsonValue
    public String json(){
        return name().toLowerCase(Locale.ROOT);
    }

    public CompanyStatus toCompanyStatus(){
        return switch (this){
            case NO_ANSWER -> CompanyStatus.NO_ANSWER;
            case REFUSED -> CompanyStatus.REFUSED;
            case MEETING_SET -> CompanyStatus.MEETING_SET;
        };

    }
}
