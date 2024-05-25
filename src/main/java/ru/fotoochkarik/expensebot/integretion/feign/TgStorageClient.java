package ru.fotoochkarik.expensebot.integretion.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.fotoochkarik.expensebot.data.dto.FileStorageResponse;

@FeignClient(
    value = "tBotClient",
    url = "${service.tg-host}"
)
public interface TgStorageClient {


  @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "bot{token}/getFile?file_id={fileId}")
  ResponseEntity<FileStorageResponse> getFilePath(@PathVariable String fileId, @PathVariable String token);

}
