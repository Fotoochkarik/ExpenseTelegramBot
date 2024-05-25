package ru.fotoochkarik.expensebot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CommandAdd extends Command {

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] strings) {
    message.setText("""
        _Please choose your expense_
        """);
    strings = addArgs(strings, "hasReplyMarkup");
    super.processMessage(absSender, message, strings);
  }

  public CommandAdd() {
    super("add", "Add your spending");
  }

}
