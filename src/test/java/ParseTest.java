import org.junit.Assert;
import org.junit.Test;
import ru.alexsumin.weightstatbot.model.UserAnswer;
import ru.alexsumin.weightstatbot.util.UserChoiceParser;

public class ParseTest {
    @Test
    public void parseCommandTest() {

        UserAnswer ans = UserChoiceParser.getUserAnswer("/start");
        Assert.assertEquals(UserAnswer.START, ans);


        UserAnswer ans2 = UserChoiceParser.getUserAnswer("/chart");
        Assert.assertEquals(UserAnswer.GET_CHART, ans2);


        UserAnswer ans3 = UserChoiceParser.getUserAnswer("12.3");
        Assert.assertEquals(UserAnswer.ADD_VALUE, ans3);

    }
}
