package ru.alexsumin.weightstatbot.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import ru.alexsumin.weightstatbot.domain.Account;
import ru.alexsumin.weightstatbot.domain.Measurement;
import ru.alexsumin.weightstatbot.service.AccountService;
import ru.alexsumin.weightstatbot.service.MeasurementService;
import ru.alexsumin.weightstatbot.util.DifferenceCalculator;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class AddNewValueCommand extends Command {

    private final Message message;
    private final AccountService accountService;
    private final MeasurementService measurementService;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");

    public AddNewValueCommand(Message message, AccountService accountService, MeasurementService measurementService) {
        this.message = message;
        this.accountService = accountService;
        this.measurementService = measurementService;
    }

    private String getCurrentDate() {
        return formatter.format(new Date(System.currentTimeMillis()));
    }


    @Override
    public CommandResponse call() throws Exception {
        Long chatId = message.getChatId();

        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(chatId);

        Account account = accountService.findById(chatId);
        String text = message.getText();

        text = text.replace(',', '.');
        BigDecimal numeric = new BigDecimal(text);

        Optional<BigDecimal> measurementLast =
                measurementService.getUserLastMeasurementValue(message.getChatId());

        Measurement measurementNew = new Measurement(numeric, account);
        measurementService.addNewValue(measurementNew);

        StringBuilder newMessage = new StringBuilder("Well, The new measurement ")
                .append(text).append(" kg was added on ").append(
                        getCurrentDate()).append(".\n").append(
                        DifferenceCalculator.getDifferenceWithSign(measurementLast, numeric));

        responseMessage.setText(newMessage.toString());
        return new CommandResponse(responseMessage);
    }
}
