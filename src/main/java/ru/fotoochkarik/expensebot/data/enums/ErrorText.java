package ru.fotoochkarik.expensebot.data.enums;

import lombok.Getter;

public enum ErrorText {

  ERROR_CONVERT_NUMBER("""
      " %s "
      _It isn`t number. Please write number_
      """),
  ERROR_MATCH_FORMAT("""
      "%s"
      _The text does not match the format:_
      "t=20220816T154500&s=1479.00&fn=9961440300456116&i=7903&fp=1439648163&n=1"
      """),
  ERROR_STICKER("""
      _The command is not recognized.
       Go back to the previous step_
                        """),
  ERROR("Error message {} and stack trace {}"),
  NOT_IMPLEMENTED("NOT_IMPLEMENTED"),
  UNEXPECTED_VALUE("Unexpected value: %s"),
  WRONG_FORMAT_DATE("_You should write date in the format: \"dd-MM-yyyy\"_"),
  NOT_CONTAIN_QUERY_TEXT("_QR does not contain the required query text_");

  ErrorText(String value) {
    this.value = value;
  }

  @Getter
  private final String value;

}
