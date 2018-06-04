package ru.alexsumin.weightstatbot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.alexsumin.weightstatbot.chart.ChartGenerator;
import ru.alexsumin.weightstatbot.domain.Account;
import ru.alexsumin.weightstatbot.domain.Measurement;
import ru.alexsumin.weightstatbot.domain.UserAnswer;
import ru.alexsumin.weightstatbot.service.AccountService;
import ru.alexsumin.weightstatbot.service.MeasurementService;
import ru.alexsumin.weightstatbot.util.DifferenceCalculator;
import ru.alexsumin.weightstatbot.util.UserChoiceParser;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Component
public class BotController extends TelegramLongPollingBot {

    private static final String HELLO = "What can this bot do? " +
            "This simple bot allows you to track " +
            "your weight change, draw a chart with of your results. " +
            "Just write /help to see a hint. " +
            "Other functionality will be soon! Have a nice day :)";

    private static final String HELP = "Now I'm going to tell you how to use me. " +
            "I keep your statistics of weight changes. " +
            "To add a new value, simply send a number. " +
            "To get a chart, send me /chart. " +
            "To get statistics, send me /stat." +
            "To delete a last value, send me /delete.";

    private static final String DELETED = "Successfully deleted last measurement!";

    private static final String NO_VALUES = "Ooups! No one measurement found. Just add a new to start!";

    private static final String UNKNOWN = "Ooups! I'm sorry, But I'm not smart guy and I don't understand you. " +
            "I can recognize only a few commands. Send /help to see a them.";

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");

    private static final int TEN_DAYS = 10;
    private static final int THIRTY_DAYS = 30;

    private static final Logger logger = LoggerFactory.getLogger(BotController.class);

    private final ThreadPoolTaskExecutor taskExecutor;
    private AccountService accountService;
    private MeasurementService measurementService;

    @Value("${bot.token}")
    private String token;
    @Value("${bot.username}")
    private String username;

    @Autowired
    public BotController(AccountService accountService, MeasurementService measurementService, ThreadPoolTaskExecutor taskExecutor) {
        this.accountService = accountService;
        this.measurementService = measurementService;
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    private void initChartGenerator() {
        ChartGenerator.initialize(taskExecutor);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            Message message = update.getMessage();
            Long chatId = message.getChatId();

            if (!accountService.isUserExists(chatId))
                sendHelloMessage(chatId);

            else {
                String text = message.getText();
                logger.info("Received text: \"" + text + "\" from user.chatId: " + chatId);
                UserAnswer answer = new UserChoiceParser(text).getUserAnswer();

                switch (answer) {
                    case GET_HELP:
                        sendHelpMessage(chatId);
                        break;
                    case ADD_VALUE:
                        addNewValue(chatId, text);
                        break;
                    case GET_STAT:
                        sendStat(chatId);
                        break;
                    case GET_CHART:
                        sendChart(chatId);
                        break;
                    case DELETE:
                        deleteLastValue(chatId);
                        break;
                    case UNKNOWN:
                        sendUnsupportedMessage(chatId);
                        break;
                }
            }
        }
    }

    private void sendUnsupportedMessage(Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(UNKNOWN);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            logger.error("Couldn't send message to user.chatId " + chatId + ". " + e.getMessage());
        }
    }

    private void deleteLastValue(Long chatId) {
        try {
            if (!measurementService.deleteLastMeasurement(chatId)) {
                noOneValueFound(chatId);
                return;
            }
            SendMessage response = new SendMessage();
            response.setChatId(chatId);
            response.setText(DELETED);
            execute(response);
        } catch (TelegramApiException e) {
            logger.error("Couldn't send message to user.chatId " + chatId + ". " + e.getMessage());
        }
    }

    private void noOneValueFound(Long chatId) throws TelegramApiException {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(NO_VALUES);
        execute(response);
    }

    private void sendChart(Long chatId) {
        List<Measurement> list = accountService.findById(chatId).getMeasurements();
        try {
            if (list.size() == 0) {
                noOneValueFound(chatId);
                return;
            }
            byte[] file = ChartGenerator.generateChart(list);
            InputStream bis = new ByteArrayInputStream(file);

            SendPhoto photoMessage = new SendPhoto().setChatId(chatId);
            photoMessage.setNewPhoto("stat.png", bis);
            sendPhoto(photoMessage);

        } catch (ChartGenerator.ChartGenerationException c) {
            logger.error("Couldn't generate a chart and send to user.chatId " + chatId + ". " + c.getMessage());
        } catch (TelegramApiException e) {
            logger.error("Couldn't send message to user.chatId " + chatId + ". " + e.getMessage());
        }
    }

    private void sendHelpMessage(Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(HELP);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            logger.error("Couldn't send message to user.chatId " + chatId + ". " + e.getMessage());
        }
    }


    private void sendHelloMessage(Long chatId) {
        SendMessage greetingMessage = new SendMessage();
        greetingMessage.setChatId(chatId);
        greetingMessage.setText(HELLO);
        try {
            execute(greetingMessage);
        } catch (TelegramApiException e) {
            logger.error("Couldn't send message to user.chatId " + chatId + ". " + e.getMessage());
        }

    }

    private void sendStat(Long chatId) {

        Optional<BigDecimal> first = measurementService.getUsersFirstMeasurementValue(chatId);

        try {
            if (!first.isPresent()) {
                noOneValueFound(chatId);
            } else {


                Optional<BigDecimal> tenDaysAgo = measurementService.getFirstMeasurementValueInPeriod(chatId, TEN_DAYS);
                Optional<BigDecimal> thirtyDaysAgo = measurementService.getFirstMeasurementValueInPeriod(chatId, THIRTY_DAYS);

                BigDecimal last = measurementService.getUsersLastMeasurementValue(chatId).get();


                SendMessage message = new SendMessage();
                message.setChatId(chatId);

                message.setText("Your current weight: " + last + " kg.\n" +
                        "Your progress in" +
                        "\n10 days: " + DifferenceCalculator.getDifferenceWithSignForStat(tenDaysAgo, last) +
                        "\n30 days: " + DifferenceCalculator.getDifferenceWithSignForStat(thirtyDaysAgo, last) +
                        "\nTotal: " + DifferenceCalculator.getDifferenceWithSignForStat(first, last)
                        + "\nTo see chart just send me /chart");

                execute(message);
            }
        } catch (TelegramApiException e) {
            logger.error("Couldn't send message to user.chatId " + chatId + ". " + e.getMessage());
        }

    }


    private void addNewValue(Long chatId, String text) {
        //it's safe, first i checked user for existing
        Account account = accountService.findById(chatId);

        text = text.replace(',', '.');
        BigDecimal numeric = new BigDecimal(text);

        Optional<BigDecimal> measurementLast = measurementService.getUsersLastMeasurementValue(chatId);

        Measurement measurementNew = new Measurement(numeric, account);
        measurementService.addNewValue(measurementNew);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        message.setText("Well, The new measurement " + text + " kg was added on " +
                getCurrentDate() + "." +
                DifferenceCalculator.getDifferenceWithSign(measurementLast, numeric)
        );

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Couldn't send message to user.chatId " + chatId + ". " + e.getMessage());
        }
    }


    private String getCurrentDate() {
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}