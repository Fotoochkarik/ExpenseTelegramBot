package ru.fotoochkarik.expensebot.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {

  @JsonProperty(value = "file_id")
  private String fileId;

  @JsonProperty(value = "file_unique_id")
  private String fileUniqueId;

  @JsonProperty(value = "file_size")
  private int fileSize;

  @JsonProperty(value = "file_path")
  private String filePath;

}

