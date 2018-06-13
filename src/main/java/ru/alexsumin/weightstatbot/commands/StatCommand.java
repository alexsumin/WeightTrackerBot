package ru.alexsumin.weightstatbot.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import ru.alexsumin.weightstatbot.service.MeasurementService;
import ru.alexsumin.weightstatbot.util.DifferenceCalculator;

import java.math.BigDecimal;
import java.util.Optional;

public class StatCommand extends Command {

    private final Message message;
    private final MeasurementService measurementService;

    private static final int TEN_DAYS = 10;
    private static final int THIRTY_DAYS = 30;

    public StatCommand(Message message, MeasurementService measurementService) {
        this.message = message;
        this.measurementService = measurementService;
    }

    @Override
    public CommandResponse call() throws Exception {
        long chatId = message.getChatId();

        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(chatId);
        Optional<BigDecimal> first = measurementService.getUserFirstMeasurementValue(chatId);

        if (!first.isPresent()) {
            responseMessage.setText("Oops! No measurements were found. Just add the new one to start!");
            return new CommandResponse(responseMessage);
        }

        Optional<BigDecimal> tenDaysAgo = measurementService.getFirstMeasurementValueInPeriod(chatId, TEN_DAYS);
        Optional<BigDecimal> thirtyDaysAgo = measurementService.getFirstMeasurementValueInPeriod(chatId, THIRTY_DAYS);

        BigDecimal last = measurementService.getUserLastMeasurementValue(chatId).get();

        StringBuilder messageText = new StringBuilder("Your current weight: ").append(last)
                .append(" kg.\n").append("Your progress within a period of").append("\n10 days: ")
                .append(DifferenceCalculator.getDifferenceWithSignForStat(tenDaysAgo, last))
                .append("\n30 days: ").append(DifferenceCalculator.getDifferenceWithSignForStat(thirtyDaysAgo, last))
                .append("\nTotal: ").append(DifferenceCalculator.getDifferenceWithSignForStat(first, last))
                .append("\nTo view a chart, just send me /chart");
        responseMessage.setText(messageText.toString());
        return new CommandResponse(responseMessage);
    }
}
