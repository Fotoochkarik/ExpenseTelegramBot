package ru.fotoochkarik.expensebot.data;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.fotoochkarik.expensebot.data.enums.Action;
import ru.fotoochkarik.expensebot.data.enums.Expense;
import ru.fotoochkarik.expensebot.service.ExpenseModeService;

public class Buttons {

  public static InlineKeyboardMarkup getInlineKeyboardMarkup(Long chatId) {
    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
    for (Expense expense : Expense.values()) {
      if (expense != Expense.PASS) {
        buttons.add(List.of(createExpenseButton(chatId, expense)));
      }
    }
    return InlineKeyboardMarkup.builder()
        .keyboard(buttons).build();
  }

  private static InlineKeyboardButton createExpenseButton(Long chatId, Expense expense) {
    return InlineKeyboardButton.builder()
        .text(getTextForExpenseButton(chatId, expense))
        .callbackData(String.format("%s:%s", Action.SPEND.name(), expense))
        .build();
  }

  private static String getTextForExpenseButton(Long chatId, Expense current) {
    Expense choseExpense = ExpenseModeService.getChoseExpense(chatId);
    return choseExpense == current ? current + " ✅" : current.name();
  }

}