package ru.fotoochkarik.expensebot.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileStorageResponse {

  private boolean ok;
  private Result result;

}
