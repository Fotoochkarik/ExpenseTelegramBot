package ru.fotoochkarik.expensebot.service;

import static java.util.Objects.nonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.fotoochkarik.expensebot.data.enums.Action;
import ru.fotoochkarik.expensebot.data.enums.Expense;

@Slf4j
@Service
public class ExpenseModeService {

  private static final Map<Long, Expense> choseExpense = new ConcurrentHashMap<>();
  private static final Map<Long, Action> choseAction = new ConcurrentHashMap<>();

  public static Action getChoseAction(long chatId) {
    final Action action = choseAction.get(chatId);
    return nonNull(action) ? action : Action.PASS;
  }

  public static void setChoseAction(long chatId, Action action) {
    choseAction.put(chatId, action);
  }

  public static void deleteChoseAction(long chatId) {
    choseAction.remove(chatId);
  }

  public static Expense getChoseExpense(long chatId) {
    final Expense expense = choseExpense.get(chatId);
    return nonNull(expense) ? expense : Expense.PASS;
  }

  public static void setChoseExpense(long chatId, Expense expense) {
    choseExpense.put(chatId, expense);
  }

  public static void deleteChoseExpense(long chatId) {
    choseExpense.remove(chatId);
  }

}
