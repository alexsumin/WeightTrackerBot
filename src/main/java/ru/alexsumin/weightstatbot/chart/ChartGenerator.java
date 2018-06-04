package ru.alexsumin.weightstatbot.chart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.alexsumin.weightstatbot.domain.Measurement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class ChartGenerator extends Application {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");
    private static Canvas canvas;
    private static volatile byte[] result;

    public static void initialize(ThreadPoolTaskExecutor taskExecutor) {
        taskExecutor.execute(() -> launch(ChartGenerator.class));

    }

    public static synchronized byte[] generateChart(List<Measurement> list) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ByteArrayOutputStream baos = null;
                try {

                    double minY = ChartHelperUtil.findMin(list);
                    double maxY = ChartHelperUtil.findMax(list);
                    double tick = ChartHelperUtil.calculateTick(minY, maxY);

                    CurvedFittedAreaChart chart = new CurvedFittedAreaChart(
                            new CategoryAxis(), new NumberAxis(minY, maxY, tick));
                    ChartHelperUtil.configureChart(chart);

                    final XYChart.Series<String, Number> series = new XYChart.Series<>();
                    for (Measurement measurement : list) {
                        series.getData().add(
                                new XYChart.Data<>(formatter.format(measurement.getDateOfMeasurement()),
                                        measurement.getAmount()));
                    }

                    chart.getData().add(series);
                    chart.setTitle("Your weight stat on "
                            + formatter.format(new Date(System.currentTimeMillis())));

                    Scene snapshotScene = new Scene(chart);
                    snapshotScene.getStylesheets().add(getClass().getClassLoader().getResource("./charts/CurveFittedChart.css").toExternalForm());

                    final SnapshotParameters snapshotParameters = new SnapshotParameters();
                    snapshotParameters.setFill(Color.TRANSPARENT);
                    WritableImage image = chart.snapshot(snapshotParameters, null);
                    BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
                    baos = new ByteArrayOutputStream();
                    ImageIO.write(bImage, "png", baos);
                    result = baos.toByteArray();

                } catch (Exception e) {
                    throw new ChartGenerationException(e);
                } finally {
                    IOUtils.closeQuietly(baos);
                }
            }
        });
        while (result == null) {
            // wait?
        }

        byte[] ret = result;
        result = null;
        return ret;
    }

    @Override
    public void start(Stage stage) {
        canvas = new Canvas();
    }


    public static class ChartGenerationException extends RuntimeException {
        public ChartGenerationException(String message) {
            super(message);
        }

        public ChartGenerationException(Throwable cause) {
            super(cause);
        }
    }

}