package ru.fotoochkarik.expensebot.data;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.fotoochkarik.expensebot.data.enums.Expense;
import ru.fotoochkarik.expensebot.service.ExpenseModeService;

public class Buttons {

  public static InlineKeyboardMarkup inlineMarkup(Long chatId) {
    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
    Expense choseExpense = ExpenseModeService.getChoseExpense(chatId);
    for (Expense expense : Expense.values()) {
      buttons.add(
          List.of(
              InlineKeyboardButton.builder()
                  .text(getButton(choseExpense, expense))
                  .callbackData("SPEND:" + expense)
                  .build()
          ));
    }
    return InlineKeyboardMarkup.builder().keyboard(buttons).build();
  }

  private static String getButton(Expense saved, Expense current) {
    return saved == current ? current + " ✅" : current.name();
  }

}