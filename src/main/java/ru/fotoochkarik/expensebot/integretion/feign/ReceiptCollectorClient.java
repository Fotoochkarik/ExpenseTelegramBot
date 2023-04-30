package ru.fotoochkarik.expensebot.integretion.feign;

import ru.fotoochkarik.expensebot.data.dto.ReceiptShortInfo;
import ru.fotoochkarik.expensebot.data.dto.SaveRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    value = "check-collector",
    url = "${feign.receipt-collector.host}"
)
public interface ReceiptCollectorClient {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ReceiptShortInfo saveReceipt(SaveRequest request);

}