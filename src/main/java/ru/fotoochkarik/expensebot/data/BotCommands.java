package ru.fotoochkarik.expensebot.data;

import java.util.List;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public interface BotCommands {

  List<BotCommand> LIST_OF_COMMANDS = List.of(
      new BotCommand("/start", "Get a welcome message"),
      new BotCommand("/add", "Add your spending"),
      new BotCommand("/download", "Download report by year"),
      new BotCommand("/help", "Info how to use this bot")
  );

}