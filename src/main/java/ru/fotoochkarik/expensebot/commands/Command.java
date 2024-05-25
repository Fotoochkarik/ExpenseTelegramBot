package ru.fotoochkarik.expensebot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.fotoochkarik.expensebot.data.Buttons;
import ru.fotoochkarik.expensebot.data.enums.ErrorText;

@Slf4j
@RequiredArgsConstructor
public abstract class Command implements IBotCommand {

  private final String commandIdentifier;
  private final String description;

  @Override
  public String getCommandIdentifier() {
    return commandIdentifier;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void processMessage(AbsSender absSender, Message message, String[] strings) {
    log.debug("COMMAND: {}({})", message.getText(), Arrays.toString(strings));
    List<String> stringList = getStringList(strings);
    boolean replyMarkup = stringList.contains("hasReplyMarkup");
    try {
      SendMessage sendMessage = SendMessage
          .builder()
          .chatId(message.getChatId().toString())
          .text(message.getText())
          .parseMode(ParseMode.MARKDOWN)
          .replyMarkup(replyMarkup ? Buttons.getInlineKeyboardMarkup(message.getChatId()) : null)
          .build();
      absSender.execute(sendMessage);
    } catch (TelegramApiException e) {
      log.error(ErrorText.ERROR.getValue(), e.getMessage(), e);
    }
  }

  protected String[] addArgs(String[] strings, String arg) {
    List<String> stringList = new ArrayList<>();
    if (strings != null && strings.length > 0) {
      stringList = Arrays.asList(strings);
    }
    stringList.add(arg);
    return stringList.toArray(String[]::new);
  }

  private List<String> getStringList(String[] strings) {
    if (strings != null && strings.length > 0) {
      return Arrays.asList(strings);
    } else {
      return new ArrayList<>();
    }
  }

}
