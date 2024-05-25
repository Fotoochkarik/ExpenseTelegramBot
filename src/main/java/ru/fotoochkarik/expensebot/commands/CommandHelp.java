package ru.fotoochkarik.expensebot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.fotoochkarik.expensebot.data.enums.ResponseText;

public class CommandHelp extends Command {

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] strings) {
    message.setText(ResponseText.HELP_TEXT.getValue());
    super.processMessage(absSender, message, strings);
  }

  public CommandHelp() {
    super("help", "Справка \\help \n");
  }

}
