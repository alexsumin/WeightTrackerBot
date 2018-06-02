package ru.alexsumin.weightstatbot.chart;

import javafx.scene.chart.AreaChart;
import ru.alexsumin.weightstatbot.model.Measurement;

import java.util.Comparator;
import java.util.List;

public class ChartHelperUtil {
    private static final int CHART_SIZE = 640;

    public static double findMax(List<Measurement> list) {
        double value = list.stream()
                .max(Comparator.comparing(Measurement::getAmount))
                .get().getAmount().doubleValue();
        return Math.ceil(value) + 1;
    }

    public static double findMin(List<Measurement> list) {
        double value = list.stream()
                .min(Comparator.comparing(Measurement::getAmount))
                .get().getAmount().doubleValue();
        return Math.floor(value) - 1;
    }

    public static double calculateTick(double min, double max) {
        double value = (max - min);
        if (value <= 3) return 0.5;
        if (value <= 7) return 1;
        return Math.floor(max - min) / 10;
    }

    public static void configureChart(AreaChart chart) {
        chart.setPrefSize(CHART_SIZE, CHART_SIZE);
        chart.setLegendVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setAlternativeRowFillVisible(false);
        chart.setAnimated(false);
    }
}
