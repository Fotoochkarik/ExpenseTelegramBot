package ru.fotoochkarik.expensebot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:application.yaml")
public class BotConfig {

  @Value("${bot.name}")
  String botName;

  @Value("${bot.token}")
  String token;

}