package ru.fotoochkarik.expensebot.service;

import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.fotoochkarik.expensebot.data.dto.CheckCollectorRequest;
import ru.fotoochkarik.expensebot.data.enums.ErrorText;
import ru.fotoochkarik.expensebot.data.enums.ResponseText;
import ru.fotoochkarik.expensebot.integretion.feign.ReceiptCollectorClient;
import ru.fotoochkarik.expensebot.util.MessageChecker;

@Service
@RequiredArgsConstructor
public class CollectorService {

  private final ReceiptCollectorClient collectorFeignClient;


  public String processAddEveryday(Message message) {
    if (!MessageChecker.checkRequest(message.getText())) {
      return String.format(ErrorText.ERROR_MATCH_FORMAT.getValue(), message.getText());
    } else {
      ExpenseModeService.deleteChoseExpense(message.getChatId());
      return addReceipt(message.getText());
    }
  }

  public String addReceipt(String queryText) {
    var bodyReceiptInfo = collectorFeignClient.addReceipt(new CheckCollectorRequest(queryText));
    return String.format(ResponseText.ADD_RECEIPT.getValue(),
        bodyReceiptInfo.dateTime().format(DateTimeFormatter.ofPattern("d M yyyy")),
        bodyReceiptInfo.totalSum()
    ).replace(".", ",");
  }

}
