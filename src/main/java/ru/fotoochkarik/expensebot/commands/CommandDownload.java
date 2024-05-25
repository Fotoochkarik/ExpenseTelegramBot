package ru.fotoochkarik.expensebot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.fotoochkarik.expensebot.data.enums.Action;
import ru.fotoochkarik.expensebot.service.ExpenseModeService;

public class CommandDownload extends Command {

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] strings) {
    message.setText("""
        _Enter the year for which you want to receive the report_
        """);
    strings = addArgs(strings, Action.DOWNLOAD_REPORT.name());
    ExpenseModeService.setChoseAction(message.getChatId(), Action.DOWNLOAD_REPORT);
    super.processMessage(absSender, message, strings);
  }

  public CommandDownload() {
    super("download", "download report");
  }

}
