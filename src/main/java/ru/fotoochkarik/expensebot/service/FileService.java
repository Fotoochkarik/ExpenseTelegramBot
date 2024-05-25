package ru.fotoochkarik.expensebot.service;

import static java.util.Objects.nonNull;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.fotoochkarik.expensebot.config.BotProperties;
import ru.fotoochkarik.expensebot.data.dto.FileStorageResponse;
import ru.fotoochkarik.expensebot.data.dto.Result;
import ru.fotoochkarik.expensebot.data.enums.ErrorText;
import ru.fotoochkarik.expensebot.integretion.feign.TgStorageClient;
import ru.fotoochkarik.expensebot.qr.QRTools;
import ru.fotoochkarik.expensebot.util.MessageChecker;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileService {

  private final BotProperties botProperties;
  private final TgStorageClient tgStorageClient;


  public String processPhoto(Message telegramMessage) {
    var photoSizeCount = telegramMessage.getPhoto().size();
    var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
    var telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
    var fileId = telegramPhoto.getFileId();
    String text = null;
    var response = tgStorageClient.getFilePath(fileId, botProperties.getToken());
    if (response.getStatusCode() == HttpStatus.OK) {
      final FileStorageResponse fileStorageResponse = response.getBody();
      if (nonNull(fileStorageResponse) && nonNull(fileStorageResponse.getResult())) {
        final Result result = fileStorageResponse.getResult();
        try {
          String fileUrl = generateUrl(result.getFilePath());
          text = QRTools.getTextFromQR(fileUrl);
          if (!MessageChecker.checkRequest(text)) {
            text = ErrorText.NOT_CONTAIN_QUERY_TEXT.getValue();
          }
        } catch (IOException e) {
          log.error("Error while get text from QR: {}", e.getMessage());
          text = e.getMessage();
        }
      }
    }
    return text;
  }

  private String generateUrl(String filePath) {
    return botProperties.getFileStorage()
        .replace("{token}", botProperties.getToken())
        .replace("{filePath}", filePath);
  }

}
