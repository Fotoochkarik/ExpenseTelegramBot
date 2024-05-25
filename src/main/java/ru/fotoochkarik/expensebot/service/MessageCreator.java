package ru.fotoochkarik.expensebot.service;

import ru.fotoochkarik.expensebot.data.enums.Expense;
import ru.fotoochkarik.expensebot.data.enums.ResponseText;

public class MessageCreator {


  public static String getMessageByExpense(Expense newExpense) {
    String text = null;
    switch (newExpense) {
      case EVERYDAY -> text = ResponseText.EVERYDAY_EXPENSE.getValue();
      case OTHER, COMMUNAL, DEBT -> text = String.format(ResponseText.OTHER_COMMUNAL_DEBT_EXPENSE.getValue(), newExpense);
    }
    return text;
  }

}
