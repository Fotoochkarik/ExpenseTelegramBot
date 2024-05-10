package ru.fotoochkarik.expensebot.service;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.fotoochkarik.expensebot.config.BotConfig;
import ru.fotoochkarik.expensebot.data.BotCommands;
import ru.fotoochkarik.expensebot.data.Buttons;
import ru.fotoochkarik.expensebot.data.dto.ExpenseRequest;
import ru.fotoochkarik.expensebot.data.dto.CheckCollectorRequest;
import ru.fotoochkarik.expensebot.data.enums.Action;
import ru.fotoochkarik.expensebot.data.enums.Expense;
import ru.fotoochkarik.expensebot.integretion.feign.InternalReportClient;
import ru.fotoochkarik.expensebot.integretion.feign.ReceiptCollectorClient;

@Slf4j
@Service
public class ExpenseTelegramBot extends TelegramLongPollingBot implements BotCommands {

  private final ReceiptCollectorClient collectorFeignClient;
  private final InternalReportClient reportClient;
  private final BotConfig botConfig;

  public ExpenseTelegramBot(ReceiptCollectorClient collectorFeignClient, InternalReportClient reportClient,
      BotConfig config) {
    this.collectorFeignClient = collectorFeignClient;
    this.reportClient = reportClient;
    this.botConfig = config;
    try {
      this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
    } catch (TelegramApiException e) {
      log.error("Error setting bot's command list: {}", e.getMessage());
    }
  }

  @Override
  public String getBotUsername() {
    return botConfig.getBotName();
  }

  @Override
  public String getBotToken() {
    return botConfig.getToken();
  }

  @Override
  @SneakyThrows
  public void onUpdateReceived(Update update) {
    if (update.hasCallbackQuery()) {
      handleCallback(update.getCallbackQuery());
    } else if (update.hasMessage()) {
      handleMessage(update.getMessage());
    }
  }

  @SneakyThrows
  private void handleCallback(CallbackQuery callbackQuery) {
    Message message = callbackQuery.getMessage();
    String[] param = callbackQuery.getData().split(":");
    String action = param[0];
    Expense newExpense = Expense.valueOf(param[1]);
    if (newExpense != Expense.EVERYDAY) {
      sendMessage(
          message.getChatId(),
          ParseMode.MARKDOWN,
          String.format(
              "Enter your %s spend in format \"amount spent(number) date(dd-MM-yyyy)\".\r\n Example: \"4500 01-04-2022\" \n or only amount spent(number) if you spent today \n Example: \"4500\"",
              newExpense.getValue())
      );
    }
    switch (action) {
      case "SPEND":
        ExpenseModeService.setChoseExpense(message.getChatId(), newExpense);
        break;
    }
    execute(
        EditMessageReplyMarkup.builder()
            .chatId(message.getChatId())
            .messageId(message.getMessageId())
            .replyMarkup(Buttons.inlineMarkup(message.getChatId()))
            .build());
  }

  private void handleMessage(Message message) {
    if (message.hasText() && message.hasEntities()) {
      Optional<MessageEntity> commandEntity = message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
      if (commandEntity.isPresent()) {
        var command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
        switch (command) {
          case "/add_spending" -> {
            log.info(" process command = {}  and text {}", command, message.getText());
            sendMessageWithButtons(message);
            return;
          }
          case "/start" -> {
            sendMessage(message.getChatId(), ParseMode.MARKDOWN, "_Welcome expense bot_");
            return;
          }
          case "/get_report_by_year" -> {
            ExpenseModeService.setChoseAction(message.getChatId(), Action.REPORT);
            sendMessage(message.getChatId(), null, "Enter year");
            return;
          }
          case "/download_report_by_year" -> {
            ExpenseModeService.setChoseAction(message.getChatId(), Action.DOWNLOAD_BY_YEAR);
            sendMessage(message.getChatId(), null, "Enter year");
            return;
          }
          case "/help" -> {
            sendMessage(message.getChatId(), null, HELP_TEXT);
            return;
          }
          default -> sendMessage(message.getChatId(), ParseMode.MARKDOWN, "_Please, send yor command_");
        }
      }
    }
    if (message.hasText()) {
      String text = message.getText();
      log.debug("Only text \"{}\"", text);
      Expense choseExpense = ExpenseModeService.getChoseExpense(message.getChatId());
      if (nonNull(choseExpense)) {
        switch (choseExpense) {
          case EVERYDAY -> {
            var bodyReceiptInfo = collectorFeignClient.saveReceipt(new CheckCollectorRequest(message.getText()));
            sendMessage(
                message.getChatId(),
                ParseMode.MARKDOWNV2,
                String.format("_Add receipt with data %s and sum %s_ \uD83E\uDDFE",
                    bodyReceiptInfo.dateTime().format(DateTimeFormatter.ofPattern("d M yyyy")),
                    bodyReceiptInfo.totalSum()
                ).replace(".", ",")
            );
            ExpenseModeService.deleteChoseExpense(message.getChatId());
            return;
          }
          case DEBT, OTHER, COMMUNAL_SERVICE -> {
            addSpendToReport(message, choseExpense);
            ExpenseModeService.deleteChoseExpense(message.getChatId());
          }
          default -> throw new IllegalStateException("Unexpected value: " + choseExpense);
        }
      }

      Action choseAction = ExpenseModeService.getChoseAction(message.getChatId());
      if (nonNull(choseAction)) {
        switch (choseAction) {
          case REPORT -> {
            reportClient.getReportYear(Integer.parseInt(message.getText()));
            ExpenseModeService.deleteChoseAction(message.getChatId());
          }
          case DOWNLOAD_BY_YEAR -> {
            HttpEntity<ByteArrayResource> downloadReport = reportClient.downloadReport(Integer.parseInt(message.getText()));
            ExpenseModeService.deleteChoseAction(message.getChatId());
            if (downloadReport.hasBody()) {
              sendFile(message, requireNonNull(downloadReport.getBody()));
            }
          }
        }
      }
    }
  }

  private void sendMessage(Long chatId, String parseDown, String text) {
    log.debug("Send message with text = {}", text);
    try {
      execute(SendMessage.builder()
          .chatId(chatId)
          .parseMode(parseDown)
          .text(text)
          .build()
      );
    } catch (TelegramApiException e) {
      log.error("error {} and stack trace {}", e.getMessage(), e.getStackTrace());
    }
  }

  @SneakyThrows
  private void sendMessageWithButtons(Message message) {
    execute(
        SendMessage.builder()
            .text("Please choose your expense")
            .chatId(message.getChatId())
            .replyMarkup(Buttons.inlineMarkup(message.getChatId()))
            .build());
  }

  @SneakyThrows
  private void sendFile(Message message, ByteArrayResource downloadReport) {
    execute(
        SendDocument.builder()
            .chatId(message.getChatId())
            .document(new InputFile(downloadReport.getInputStream(), downloadReport.getFilename()))
            .build()
    );
  }

  private void addSpendToReport(Message message, Expense choseExpense) {
    String[] splitRequest = message.getText().trim().split(" ");
    log.info(Arrays.toString(splitRequest));
    var sum = convertSum(message.getChatId(), splitRequest[0]);
    var requestDate = LocalDate.now();
    if (splitRequest.length > 1) {
      requestDate  = LocalDate.parse(splitRequest[1], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    if (nonNull(sum)) {
      var expenseResponse = reportClient.addSpendItem(
          ExpenseRequest.builder()
              .payDate(requestDate)
              .sum(sum)
              .type(choseExpense)
              .build()
      );
      sendMessage(
          message.getChatId(),
          ParseMode.MARKDOWNV2,
          String.format("_Add %s sum %s to %s %s and total sum: %s_ \uD83D\uDCC4",
              expenseResponse.getType().getValue(),
              expenseResponse.getSum(),
              expenseResponse.getMonth(),
              expenseResponse.getYear(),
              expenseResponse.getTotalSum()
          ).replace(".", ",")
      );
    }
  }

  private Double convertSum(Long chatId, String text) {
    try {
      return Double.parseDouble(text);
    } catch (NumberFormatException e) {
      log.error(e.getMessage());
      sendMessage(chatId, ParseMode.MARKDOWNV2, "_You should only specify the amount in number format for your expenses_");
    }
    return null;
  }

}
