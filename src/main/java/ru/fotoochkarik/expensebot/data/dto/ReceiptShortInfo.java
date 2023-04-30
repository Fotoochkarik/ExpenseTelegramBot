package ru.fotoochkarik.expensebot.data.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReceiptShortInfo(LocalDateTime dateTime,
                               float totalSum,
                               List<ItemShortInfo> items) {

}