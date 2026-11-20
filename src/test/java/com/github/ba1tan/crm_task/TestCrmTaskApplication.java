package com.github.ba1tan.crm_task;

import org.springframework.boot.SpringApplication;

public class TestCrmTaskApplication {

	public static void main(String[] args) {
		SpringApplication.from(CrmTaskApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
