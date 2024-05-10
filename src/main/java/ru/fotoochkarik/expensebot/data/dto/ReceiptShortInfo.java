package ru.fotoochkarik.expensebot.data.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReceiptShortInfo(LocalDateTime dateTime,
                               Double totalSum,
                               List<ItemShortInfo> items) {

}