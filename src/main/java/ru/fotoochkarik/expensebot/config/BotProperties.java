package ru.fotoochkarik.expensebot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:application.yaml")
public class BotProperties {

  @Value("${service.bot.name}")
  String botName;

  @Value("${service.bot.token}")
  String token;

  @Value("${service.file_storage.uri}")
  String fileStorage;

}