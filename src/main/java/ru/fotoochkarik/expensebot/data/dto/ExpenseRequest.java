package ru.fotoochkarik.expensebot.data.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.fotoochkarik.expensebot.data.enums.Expense;

@Getter
@Setter
@Builder
public class ExpenseRequest {

  private Expense type;
  private double sum;
  private ZonedDateTime payDate;

}
