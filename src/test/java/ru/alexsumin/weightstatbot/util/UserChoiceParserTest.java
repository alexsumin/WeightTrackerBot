package ru.alexsumin.weightstatbot.util;

import org.junit.Assert;
import org.junit.Test;
import ru.alexsumin.weightstatbot.domain.UserAnswer;

public class UserChoiceParserTest {
    @Test
    public void parseCommandTest() {

        UserAnswer answerStart = new UserChoiceParser("/start").getUserAnswer();
        Assert.assertEquals(UserAnswer.START, answerStart);

        UserAnswer answerChart = new UserChoiceParser("/chart").getUserAnswer();
        Assert.assertEquals(UserAnswer.GET_CHART, answerChart);

        UserAnswer answerAddValue = new UserChoiceParser("12.5").getUserAnswer();
        Assert.assertEquals(UserAnswer.ADD_VALUE, answerAddValue);

        UserAnswer answerHelp = new UserChoiceParser("/hElP").getUserAnswer();
        Assert.assertEquals(UserAnswer.GET_HELP, answerHelp);

        UserAnswer answerStat = new UserChoiceParser("/sTAT").getUserAnswer();
        Assert.assertEquals(UserAnswer.GET_STAT, answerStat);

        UserAnswer answerDelete = new UserChoiceParser("/dElEtE").getUserAnswer();
        Assert.assertEquals(UserAnswer.DELETE, answerDelete);

        UserAnswer answerUnknown = new UserChoiceParser("/unknown_text").getUserAnswer();
        Assert.assertEquals(UserAnswer.UNKNOWN, answerUnknown);

        UserAnswer answerAddValueAnother = new UserChoiceParser("99,99").getUserAnswer();
        Assert.assertEquals(UserAnswer.ADD_VALUE, answerAddValueAnother);

    }
}
