package org.app.invitationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InvitationServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(InvitationServiceApplication.class, args);
  }

}
