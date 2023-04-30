package ru.fotoochkarik.expensebot;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ExpenseBotApplication {

  @SneakyThrows
  public static void main(String[] args) {
    SpringApplication.run(ExpenseBotApplication.class, args);
  }

}
