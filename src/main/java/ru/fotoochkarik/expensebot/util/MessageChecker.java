package ru.fotoochkarik.expensebot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageChecker {

  public boolean checkRequest(final String input) {
    final Pattern pattern = Pattern.compile("t=[0-9]+T[0-9]+&s=[0-9]*\\.[0-9]+&fn=[0-9]+&i=[0-9]+&fp=[0-9]+&n=[0-9]+", Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher(input);
    return matcher.matches();
  }

}
