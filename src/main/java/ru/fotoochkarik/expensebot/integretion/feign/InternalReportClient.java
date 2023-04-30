package ru.fotoochkarik.expensebot.integretion.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.fotoochkarik.expensebot.data.dto.ExpenseRequest;
import ru.fotoochkarik.expensebot.data.dto.ExpenseResponse;

@FeignClient(
    value = "internal",
    url = "${feign.report.host}"
)
public interface InternalReportClient {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/add")
  ExpenseResponse addSpendItem(ExpenseRequest expenseRequest);

  @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value ="/report-year")
  ResponseEntity<Void> getReportYear(@RequestParam("year") Integer year);

  @GetMapping(consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, value="/download-report")
  HttpEntity<ByteArrayResource> downloadReport(@RequestParam("year") Integer year);

}
