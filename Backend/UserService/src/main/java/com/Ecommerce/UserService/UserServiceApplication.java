package com.Ecommerce.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserServiceApplication {

  static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }

}
