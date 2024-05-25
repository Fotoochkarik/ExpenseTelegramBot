package ru.fotoochkarik.expensebot.data.enums;

import lombok.Getter;

public enum Expense {

  EVERYDAY("everyday"),
  DEBT("debt"),
  COMMUNAL("communal"),
  OTHER("other"),
  PASS("pass");

  Expense(String value) {
    this.value = value;
  }

  @Getter
  private final String value;

}
