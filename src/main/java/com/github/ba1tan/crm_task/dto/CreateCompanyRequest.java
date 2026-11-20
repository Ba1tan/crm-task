package com.github.ba1tan.crm_task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCompanyRequest(@NotBlank @Size(max = 255)
                                   String name,
                                   @NotBlank @Pattern(regexp = "\\d{12}", message = "БИН должен состоять из 12 цифр")
                                   String bin,
                                   @NotBlank @Size(max = 20)
                                   String phone) {
}
