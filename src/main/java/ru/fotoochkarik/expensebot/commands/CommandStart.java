package ru.fotoochkarik.expensebot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CommandStart extends Command {

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] strings) {
    message.setText(String.format("""
        _Welcome %s!
        It is expense bot_""", message.getFrom().getUserName()));
    super.processMessage(absSender, message, null);
  }

  public CommandStart() {
    super("start", "Start expense bot");
  }

}
