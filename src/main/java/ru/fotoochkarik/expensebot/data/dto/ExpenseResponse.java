package ru.fotoochkarik.expensebot.data.dto;

import java.time.Month;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.fotoochkarik.expensebot.data.enums.Expense;

@Getter
@Setter
@Builder
public class ExpenseResponse {

  private Expense type;
  private double sum;
  private double totalSum;
  private Month month;
  private Integer year;

}
