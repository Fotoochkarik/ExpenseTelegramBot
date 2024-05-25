package ru.fotoochkarik.expensebot.integretion.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import ru.fotoochkarik.expensebot.data.dto.CheckCollectorRequest;
import ru.fotoochkarik.expensebot.data.dto.ReceiptShortInfo;

@FeignClient(
    value = "check-collector",
    url = "${feign.receipt-collector.host}"
)
public interface ReceiptCollectorClient {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/api/latest/receipt")
  ReceiptShortInfo addReceipt(CheckCollectorRequest request);

}