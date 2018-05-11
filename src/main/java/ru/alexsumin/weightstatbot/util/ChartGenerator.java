package ru.alexsumin.weightstatbot.util;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.alexsumin.weightstatbot.Main;
import ru.alexsumin.weightstatbot.model.Measurement;

import javax.imageio.ImageIO;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class ChartGenerator extends Application {

    private static final int CHART_SIZE = 640;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM");
    private static Canvas canvas;
    private static volatile boolean result;
    private static String newPicture;

    public static void initialize(ThreadPoolTaskExecutor taskExecutor) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                launch(ChartGenerator.class);
            }
        });

    }

    public static synchronized File generateChart(List<Measurement> list, long chatId) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                result = false;
                try {
                    final Axis xAxis = new CategoryAxis();
                    final Axis yAxis = new NumberAxis();
                    final LineChart<String, Number> chart = new LineChart(xAxis, yAxis);

                    ObservableList<XYChart.Series<String, Number>> data =
                            FXCollections.observableArrayList();

                    XYChart.Series<String, Number> as = new XYChart.Series<>();

                    for (Measurement v : list) {
                        as.getData().add(new XYChart.Data<>
                                (formatter.format(v.getDateOfMeasurement()), v.getAmount()));
                    }


                    Date currentDate = new Date(System.currentTimeMillis());

                    data.addAll(as);
                    chart.setData(data);
                    chart.setTitle("Your weight stat on " + formatter.format(currentDate));
                    chart.setLegendVisible(false);


                    chart.setAnimated(false);
                    chart.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                    chart.setPrefSize(CHART_SIZE, CHART_SIZE);
                    chart.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);


                    Scene snapshotScene = new Scene(chart);


                    snapshotScene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());

                    WritableImage image = chart.snapshot(new SnapshotParameters(), null);
                    newPicture = Main.PICTURE_FOLDER + File.separator + chatId + "_" + formatter.format(currentDate) + ".png";

                    File newFile = new File(newPicture);

                    newFile.createNewFile();

                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", newFile);


                    result = true;


                } catch (Exception e) {
                    throw new ChartGenerationException(e);
                }
            }
        });
        while (result == false) {
            // wait?
        }
        result = false;
        File file = new File(newPicture);
        return file;
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