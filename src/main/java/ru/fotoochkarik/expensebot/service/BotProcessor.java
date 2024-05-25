package ru.fotoochkarik.expensebot.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.fotoochkarik.expensebot.data.BotCommands.LIST_OF_COMMANDS;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.fotoochkarik.expensebot.commands.CommandAdd;
import ru.fotoochkarik.expensebot.commands.CommandDownload;
import ru.fotoochkarik.expensebot.commands.CommandHelp;
import ru.fotoochkarik.expensebot.commands.CommandStart;
import ru.fotoochkarik.expensebot.config.BotProperties;
import ru.fotoochkarik.expensebot.data.Buttons;
import ru.fotoochkarik.expensebot.data.enums.Action;
import ru.fotoochkarik.expensebot.data.enums.ErrorText;
import ru.fotoochkarik.expensebot.data.enums.Expense;
import ru.fotoochkarik.expensebot.util.MessageChecker;

@RequiredArgsConstructor
@Component
@Slf4j
public class BotProcessor extends TelegramLongPollingCommandBot {

  private final BotProperties botProperties;
  private final ReportService reportService;
  private final CollectorService collectorService;
  private final FileService fileService;

  @PostConstruct
  void init() {
    try {
      this.execute(
          new SetMyCommands(LIST_OF_COMMANDS,
              new BotCommandScopeDefault(),
              null));
    } catch (TelegramApiException e) {
      log.error("Error setting bot's command list: {}", e.getMessage());
    }
    registerAll(new CommandHelp(), new CommandStart(), new CommandDownload(), new CommandAdd());
  }

  @Override
  public String getBotUsername() {
    return botProperties.getBotName();
  }

  @Override
  public String getBotToken() {
    return botProperties.getToken();
  }

  @Override
  public void processNonCommandUpdate(Update update) {
    if (update.hasMessage()) {
      processMessage(update.getMessage());
    } else if (update.hasCallbackQuery()) {
      processCallbackQuery(update.getCallbackQuery());
    }
  }

  private void processMessage(Message message) {
    String response = null;
    if (message.hasText()) {
      response = processText(message);
    }
    if (message.hasPhoto()) {
      response = fileService.processPhoto(message);
      if (MessageChecker.checkRequest(response)) {
        response = collectorService.addReceipt(response);
      }
    }
    if (message.hasSticker()) {
      response = ErrorText.ERROR_STICKER.getValue();
    }
    sendMessage(message.getChatId(), response);
  }

  private void processCallbackQuery(CallbackQuery callbackQuery) {
    Message message = callbackQuery.getMessage();
    String[] param = callbackQuery.getData().split(":");
    var action = Action.valueOf(param[0]);
    Expense newExpense = Expense.valueOf(param[1]);
    var messageByExpense = MessageCreator.getMessageByExpense(newExpense);
    sendMessage(message.getChatId(), messageByExpense);

    switch (action) {
      case SPEND -> ExpenseModeService.setChoseExpense(message.getChatId(), newExpense);
    }
    InlineKeyboardMarkup inlineKeyboardMarkup = Buttons.getInlineKeyboardMarkup(message.getChatId());
    sendInlineKeyboardMarkup(message, inlineKeyboardMarkup);
  }

  private String processText(Message message) {
    String response;
    response = getExpenseResponse(message);
    Action choseAction = ExpenseModeService.getChoseAction(message.getChatId());
    switch (choseAction) {
      case REPORT -> processReport(message, message.getText());
      case DOWNLOAD_REPORT -> processDownloadReport(message, message.getText());
    }
    if (MessageChecker.checkRequest(message.getText())) {
      response = collectorService.addReceipt(message.getText());
    }
    return response;
  }

  private String getExpenseResponse(Message message) {
    Expense choseExpense = ExpenseModeService.getChoseExpense(message.getChatId());
    switch (choseExpense) {
      case EVERYDAY -> {
        return collectorService.processAddEveryday(message);
      }
      case DEBT, OTHER, COMMUNAL -> {
        return reportService.addSpendToReport(message, choseExpense);
      }
      case PASS -> {
        return null;
      }
      default -> {
        return String.format(ErrorText.UNEXPECTED_VALUE.getValue(), choseExpense);
      }
    }
  }

  private void processDownloadReport(Message message, String text) {
    var year = getYearFromText(message.getChatId(), text);
    final InputFile reportFile = reportService.getReportFile(year);
    if (nonNull(reportFile)) {
      sendFile(message, reportFile);
    }
    ExpenseModeService.deleteChoseAction(message.getChatId());
  }

  private void processReport(Message message, String text) {
    var year = getYearFromText(message.getChatId(), text);
    if (nonNull(year)) {
      reportService.getReportYear(year);
      ExpenseModeService.deleteChoseAction(message.getChatId());
    }
  }

  private Integer getYearFromText(Long chatId, String text) {
    Integer year = null;
    try {
      year = Integer.parseInt(text);
    } catch (NumberFormatException e) {
      log.error(ErrorText.ERROR.getValue(), e.getMessage(), e.getStackTrace());
      sendMessage(chatId, String.format(ErrorText.ERROR_CONVERT_NUMBER.getValue(), text));
    }
    return year;
  }

  private void sendInlineKeyboardMarkup(Message message, InlineKeyboardMarkup inlineKeyboardMarkup) {
    final Long chatId = message.getChatId();
    final Integer messageId = message.getMessageId();
    if (isNull(chatId) || isNull(messageId) || isNull(inlineKeyboardMarkup)) {
      return;
    }
    try {
      execute(
          EditMessageReplyMarkup.builder()
              .chatId(chatId)
              .messageId(messageId)
              .replyMarkup(inlineKeyboardMarkup)
              .build());
    } catch (TelegramApiException e) {
      log.error(ErrorText.ERROR.getValue(), e.getMessage(), e.getStackTrace());
    }
  }

  private void sendMessage(Long chatId, String text) {
    if (isNull(text) || isNull(chatId)) {
      return;
    }
    try {
      execute(SendMessage.builder()
          .chatId(chatId)
          .parseMode(ParseMode.MARKDOWN)
          .text(text)
          .build()
      );
    } catch (TelegramApiException e) {
      log.error(ErrorText.ERROR.getValue(), e.getMessage(), e.getStackTrace());
    }
  }

  private void sendFile(Message message, InputFile file) {
    final Long chatId = message.getChatId();
    if (isNull(chatId) || isNull(file)) {
      return;
    }
    try {
      execute(
          SendDocument.builder()
              .chatId(chatId)
              .document(file)
              .build()
      );
    } catch (TelegramApiException e) {
      log.error(ErrorText.ERROR.getValue(), e.getMessage(), e.getStackTrace());
    }
  }

}
