package com.rkumar0206.mymexpensecategoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MymExpenseCategoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MymExpenseCategoryServiceApplication.class, args);
	}

}
