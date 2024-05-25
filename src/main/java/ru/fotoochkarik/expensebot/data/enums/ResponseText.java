package ru.fotoochkarik.expensebot.data.enums;

import lombok.Getter;

public enum ResponseText {

  ADD_RECEIPT("_Add receipt with data %s and sum %s_ \uD83E\uDDFE"),
  HELP_TEXT("""
      _This bot will help to count the number of messages in the chat._
                  
      The following commands are available to you:
            
      /start - start the bot
      /add - add your spending
      /download - download report by year
      /help - help menu;"""),
  EVERYDAY_EXPENSE("""
      _The text must match the format:_
                
      "t=20220816T154500&s=1479.00&fn=9961440300456116&i=7903&fp=1439648163&n=1
      """),
  OTHER_COMMUNAL_DEBT_EXPENSE("""
      _Enter your %s spend in format "amount spent(number) date(dd-MM-yyyy)"_
                
      Example: "4500 01-04-2022"
                
      _Or only amount spent(number) if you spent today_
                
      Example: "4500"
      """);

  ResponseText(String value) {
    this.value = value;
  }

  @Getter
  private final String value;

}
