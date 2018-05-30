package ru.alexsumin.weightstatbot.util;

import org.junit.Before;
import org.junit.Test;
import ru.alexsumin.weightstatbot.model.Account;
import ru.alexsumin.weightstatbot.model.Measurement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ChartHelperUtilTest {

    List<Measurement> list;
    double delta = 0.001;

    @Before
    public void setUp() throws Exception {

        Account account = new Account();
        list = new ArrayList<>();
        list.add(new Measurement(BigDecimal.valueOf(40.7), account));
        list.add(new Measurement(BigDecimal.valueOf(34.2), account));
        list.add(new Measurement(BigDecimal.valueOf(25.1), account));
        list.add(new Measurement(BigDecimal.valueOf(35.1), account));
        list.add(new Measurement(BigDecimal.valueOf(45.9), account));
        list.add(new Measurement(BigDecimal.valueOf(50.0), account));
        list.add(new Measurement(BigDecimal.valueOf(65.2), account));
        list.add(new Measurement(BigDecimal.valueOf(60.4), account));
    }

    @Test
    public void findMax() {
        double calculated = ChartHelperUtil.findMax(list);
        assertEquals(67, calculated, delta);
    }

    @Test
    public void findMin() {
        double calculated = ChartHelperUtil.findMin(list);
        assertEquals(24, calculated, delta);
    }

    @Test
    public void calculateTickTest() {

        //24
        double min = ChartHelperUtil.findMin(list);

        //67
        double max = ChartHelperUtil.findMax(list);

        assertEquals(4.3, ChartHelperUtil.calculateTick(min, max), delta);
    }
}