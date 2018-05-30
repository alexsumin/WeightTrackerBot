package ru.alexsumin.weightstatbot.util;

import org.junit.Assert;
import org.junit.Test;
import ru.alexsumin.weightstatbot.model.UserAnswer;

public class UserChoiceParserTest {
    @Test
    public void parseCommandTest() {

        UserAnswer answerStart = UserChoiceParser.getUserAnswer("/start");
        Assert.assertEquals(UserAnswer.START, answerStart);

        UserAnswer answerChart = UserChoiceParser.getUserAnswer("/chart");
        Assert.assertEquals(UserAnswer.GET_CHART, answerChart);

        UserAnswer answerAddValue = UserChoiceParser.getUserAnswer("12.3");
        Assert.assertEquals(UserAnswer.ADD_VALUE, answerAddValue);

        UserAnswer answerHelp = UserChoiceParser.getUserAnswer("/hElP");
        Assert.assertEquals(UserAnswer.GET_HELP, answerHelp);

        UserAnswer answerStat = UserChoiceParser.getUserAnswer("/sTAT");
        Assert.assertEquals(UserAnswer.GET_STAT, answerStat);

        UserAnswer answerDelete = UserChoiceParser.getUserAnswer("/dElEtE");
        Assert.assertEquals(UserAnswer.DELETE, answerDelete);

        UserAnswer answerUnknown = UserChoiceParser.getUserAnswer("/unknown_text");
        Assert.assertEquals(UserAnswer.UNKNOWN, answerUnknown);

        UserAnswer answerAddValueAnother = UserChoiceParser.getUserAnswer("99,99");
        Assert.assertEquals(UserAnswer.ADD_VALUE, answerAddValueAnother);

    }
}
