package ru.fotoochkarik.expensebot.data.enums;

import lombok.Getter;

public enum Expense {

  EVERYDAY("everyday"),
  DEBT("debt"),
  COMMUNAL_SERVICE("communal service"),
  OTHER("other");

  Expense(String value) {
    this.value = value;
  }

  @Getter
  private final String value;

}
