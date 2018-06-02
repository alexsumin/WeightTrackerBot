package ru.alexsumin.weightstatbot.util;

import java.math.BigDecimal;
import java.util.Optional;

public class DifferenceCalculator {

    private DifferenceCalculator() {
    }

    public static String getDifferenceWithSign(Optional<BigDecimal> forCheck, BigDecimal secondValue) {

        if (!forCheck.isPresent()) return "";

        BigDecimal firstValue = forCheck.get();
        BigDecimal result = secondValue.subtract(firstValue);

        if (result.doubleValue() < 0)
            return " Last change: " + result + " kg.";
        else return " Last change: +" + result + " kg.";
    }

    public static String getDifferenceWithSignForStat(Optional<BigDecimal> first, BigDecimal second) {
        if (!first.isPresent()) return "0 kg";
        BigDecimal result = first.get().subtract(second);

        if (result.doubleValue() > 0)
            return "+" + result + " kg";
        else return result + " kg";
    }
}
