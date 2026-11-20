package com.github.ba1tan.crm_task.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import com.github.ba1tan.crm_task.exception.ApiException;

import java.util.Locale;

public enum CompanyStatus {
    NEW,
    NO_ANSWER,
    REFUSED,
    MEETING_SET;
    @JsonValue
    public String json(){
        return name().toLowerCase(Locale.ROOT);
    }

    public static CompanyStatus parse(String s){
        for(CompanyStatus v : values()){
            if(v.name().equalsIgnoreCase(s)) return v;
        }
        throw ApiException.badRequest("Недопустимый статус " + s);
    }
}
