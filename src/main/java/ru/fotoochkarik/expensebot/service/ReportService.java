package ru.fotoochkarik.expensebot.service;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.fotoochkarik.expensebot.data.dto.ExpenseRequest;
import ru.fotoochkarik.expensebot.data.enums.ErrorText;
import ru.fotoochkarik.expensebot.data.enums.Expense;
import ru.fotoochkarik.expensebot.integretion.feign.InternalReportClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

  private final InternalReportClient reportClient;


  public void getReportYear(Integer year) {
    reportClient.getReportYear(year);
  }

  public InputFile getReportFile(Integer year) {
    HttpEntity<ByteArrayResource> reportHttpEntity = reportClient.downloadReport(year);
    InputFile file = null;
    if (reportHttpEntity.hasBody()) {
      var downloadReport = reportHttpEntity.getBody();
      try {
        file = new InputFile(requireNonNull(downloadReport).getInputStream(), downloadReport.getFilename());
      } catch (IOException e) {
        log.error(ErrorText.ERROR.getValue(), e.getMessage(), e);
      }
    }
    return file;
  }


  public String addSpendToReport(Message message, Expense choseExpense) {
    String[] splitRequest = message.getText().trim().split(" ");
    var sum = convertSum(splitRequest[0]);
    var requestDate = LocalDate.now();
    if (splitRequest.length > 1) {
      try {
        requestDate = LocalDate.parse(splitRequest[1], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
      } catch (DateTimeParseException e) {
        log.error(e.getMessage());
        return ErrorText.WRONG_FORMAT_DATE.getValue();
      }
    }
    if (nonNull(sum)) {
      var expenseResponse = reportClient.addSpendItem(
          ExpenseRequest.builder()
              .payDate(requestDate)
              .sum(sum)
              .type(choseExpense)
              .build()
      );
      ExpenseModeService.deleteChoseExpense(message.getChatId());
      return String.format("_Add %s sum %s to %s %s and total sum: %s_ \uD83D\uDCC4",
          expenseResponse.getType().getValue(),
          expenseResponse.getSum(),
          expenseResponse.getMonth(),
          expenseResponse.getYear(),
          expenseResponse.getTotalSum()
      ).replace(".", ",");
    }
    return "_You should only specify the amount in number format for your expenses_";
  }

  private Double convertSum(String text) {
    try {
      return Double.parseDouble(text);
    } catch (NumberFormatException e) {
      log.error(e.getMessage());
    }
    return null;
  }

}
