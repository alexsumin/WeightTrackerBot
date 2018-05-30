package ru.alexsumin.weightstatbot.util;

import org.springframework.util.NumberUtils;
import ru.alexsumin.weightstatbot.model.UserAnswer;

import java.math.BigDecimal;

public class UserChoiceParser {

    private UserChoiceParser() {
    }


    public static synchronized UserAnswer getUserAnswer(String text) {

        switch (text.toLowerCase()) {

            case ("/start"):
                return UserAnswer.START;
            case ("/help"):
                return UserAnswer.GET_HELP;
            case ("/chart"):
                return UserAnswer.GET_CHART;
            case ("/stat"):
                return UserAnswer.GET_STAT;
            case ("/delete"):
                return UserAnswer.DELETE;
            default: {
                try {
                    text = text.replace(',', '.');
                    BigDecimal value = NumberUtils.parseNumber(text, BigDecimal.class);
                    return UserAnswer.ADD_VALUE;
                } catch (NumberFormatException e) {
                    return UserAnswer.UNKNOWN;
                }
            }

        }

    }

}
