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
import ru.alexsumin.weightstatbot.model.Account;
import ru.alexsumin.weightstatbot.model.Measurement;
import ru.alexsumin.weightstatbot.model.UserAnswer;
import ru.alexsumin.weightstatbot.repository.AccountRepository;
import ru.alexsumin.weightstatbot.repository.MeasurementRepository;
import ru.alexsumin.weightstatbot.util.ChartGenerator;
import ru.alexsumin.weightstatbot.util.UserChoiceParser;

import javax.annotation.PostConstruct;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BotController extends TelegramLongPollingBot {

    public static final String HELLO = "What can this bot do? " +
            "This simple bot allows you to track " +
            "your weight change, draw a chart with of your results. " +
            "Just write /help to see a hint. " +
            "Other functionality will be soon! Have a nice day :)";

    public static final String HELP = "Now I'm going to tell you how to use me. " +
            "I keep your statistics of weight changes. " +
            "To add a new value, simply send a number. " +
            "To get a chart, send me /chart. " +
            "To get statistics, send me /stat." +
            "To delete a last value, send me /delete.";

    public static final String DELETED = "Successfully deleted last measurement!";

    public static final String NO_VALUES = "Ooups! No one measurement found. Just add a new to start!";

    public static final String UNKNOWN = "Ooups! I'm sorry, But I'm not smart guy and I don't understand you. " +
            "I can recognize only a few commands. Send /help to see a them.";


    private static final long TEN_DAYS = 10 * 24 * 60 * 60 * 1000;
    private static final long THIRTY_DAYS = 30 * 24 * 60 * 60 * 1000;

    private static final Logger logger = LoggerFactory.getLogger(BotController.class);

    private final ThreadPoolTaskExecutor taskExecutor;
    private AccountRepository accountRepository;
    private MeasurementRepository measurementRepository;

    @Value("${bot.token}")
    private String token;
    @Value("${bot.username}")
    private String username;

    @Autowired
    public BotController(AccountRepository accountRepository, MeasurementRepository measurementRepository, ThreadPoolTaskExecutor taskExecutor) {
        this.accountRepository = accountRepository;
        this.measurementRepository = measurementRepository;
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

            if (!isUserExist(chatId)) {
                registerUser(chatId);
                sendHelloMessage(chatId);
                return;
            } else {
                String text = message.getText();
                UserAnswer answer = UserChoiceParser.getUserAnswer(text);

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
            e.printStackTrace();
        }
    }

    private void deleteLastValue(Long chatId) {
        Optional<BigInteger> measurement = measurementRepository.getUsersLastMeasurementId(chatId);
        try {
            if (!measurement.isPresent()) {
                noOneValueFound(chatId);
                return;
            }
            Long lastMeasurement = measurement.get().longValue();
            measurementRepository.deleteById(lastMeasurement);

            SendMessage response = new SendMessage();
            response.setChatId(chatId);
            response.setText(DELETED);

            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void noOneValueFound(Long chatId) throws TelegramApiException {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(NO_VALUES);
        execute(response);
    }

    private void sendChart(Long chatId) {
        List<Measurement> list = accountRepository.findByChatId(chatId).get().getMeasurements();
        try {
            if (list.size() == 0) {
                noOneValueFound(chatId);
                return;
            }
            File file = ChartGenerator.generateChart(list, chatId);

            SendPhoto photoMessage = new SendPhoto().setChatId(chatId);
            photoMessage.setNewPhoto(file);
            sendPhoto(photoMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendHelpMessage(Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(HELP);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void registerUser(Long chatId) {
        Account newUser = new Account(chatId);
        accountRepository.save(newUser);
    }

    public void sendHelloMessage(Long chatId) {
        SendMessage greetingMessage = new SendMessage();
        greetingMessage.setChatId(chatId);
        greetingMessage.setText(HELLO);
        try {
            execute(greetingMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public boolean isUserExist(Long chatId) {
        Optional<Account> user = accountRepository.findByChatId(chatId);
        return (user.isPresent());
    }

    private void sendStat(Long chatId) {

        Account account = accountRepository.findByChatId(chatId).get();
        Optional<BigInteger> first = measurementRepository.getUsersFirstMeasurementId(chatId);

        try {
            if (!first.isPresent()) {
                noOneValueFound(chatId);
            } else {

                BigDecimal firstMeasurement = measurementRepository.findById(first.get().longValue()).get().getAmount();
                List<Measurement> measurements = account.getMeasurements();

                BigDecimal tenDaysAgo = getFirstMeasurementInPeriod(measurements, TEN_DAYS);
                BigDecimal thirtyDaysAgo = getFirstMeasurementInPeriod(measurements, THIRTY_DAYS);


                BigDecimal last = measurementRepository.findById
                        (measurementRepository.getUsersLastMeasurementId(chatId).get()
                                .longValue()).get().getAmount();

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Your current weight: " + last + " kg.\n" +
                        "Your progress in" +
                        "\n10 days: " + getDifferenceWithSignForStat(tenDaysAgo, last) +
                        "\n30 days: " + getDifferenceWithSignForStat(thirtyDaysAgo, last) +
                        "\nTotal: " + getDifferenceWithSignForStat(firstMeasurement, last)
                        + "\nTo see chart just send me /chart");

                execute(message);
            }


        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private BigDecimal getFirstMeasurementInPeriod(List<Measurement> list, long period) {
        List<Measurement> tenDaysAgoList = list.stream().
                filter(m -> m.getDateOfMeasurement().getTime() <= (new Date(System.currentTimeMillis()).getTime() - period)).
                collect(Collectors.toList());
        BigDecimal value = null;
        if (tenDaysAgoList.size() != 0)
            value = tenDaysAgoList.get(0).getAmount();
        return value;
    }


    private void addNewValue(Long chatId, String text) {
        //it's safe, first i checked user for existing
        Account account = accountRepository.findByChatId(chatId).get();

        text = text.replace(',', '.');
        BigDecimal numeric = new BigDecimal(text);

        Optional<BigInteger> last = measurementRepository.getUsersLastMeasurementId(chatId);

        Measurement measurement = new Measurement(numeric, account);
        measurementRepository.save(measurement);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Well, The new measurement " + text + " kg was added on " +
                new Date(System.currentTimeMillis()) + "." +
                getDifferenceWithSign(last, numeric));

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private String getDifferenceWithSignForStat(BigDecimal first, BigDecimal second) {
        if (first == null) return "0 kg";
        BigDecimal result = first.subtract(second);

        if (result.doubleValue() < 0)
            return result + " kg";
        else return "-" + result + " kg";
    }

    private String getDifferenceWithSign(Optional<BigInteger> forCheck, BigDecimal secondValue) {

        if (!forCheck.isPresent()) return "";

        BigDecimal firstValue = measurementRepository.findById(forCheck.get().longValue()).get().getAmount();
        BigDecimal result = secondValue.subtract(firstValue);

        if (result.doubleValue() < 0)
            return " Last change: " + result + " kg.";
        else return " Last change: +" + result + " kg.";
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
