package ru.fotoochkarik.expensebot.data;

import java.util.List;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public interface BotCommands {

  List<BotCommand> LIST_OF_COMMANDS = List.of(
      new BotCommand("/start", "Get a welcome message"),
      new BotCommand("/add_spending", "Add your spending"),
      new BotCommand("/get_report_by_year", "Get report by year"),
      new BotCommand("/download_report_by_year", "Download report by year"),
      new BotCommand("/help", "Info how to use this bot")
  );

  String HELP_TEXT = "This bot will help to count the number of messages in the chat. " +
      "The following commands are available to you: \n\n" +
      "/start - start the bot\n" +
      "/add_spending - add your spending\n" +
      "/get_report_by_year- get report by year\n" +
      "/download_report_by_year - download report by year\n" +
      "/help - help menu";

}