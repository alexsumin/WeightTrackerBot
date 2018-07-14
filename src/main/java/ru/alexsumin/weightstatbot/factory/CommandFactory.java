package ru.alexsumin.weightstatbot.factory;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import ru.alexsumin.weightstatbot.commands.*;
import ru.alexsumin.weightstatbot.domain.UserAnswer;
import ru.alexsumin.weightstatbot.service.AccountService;
import ru.alexsumin.weightstatbot.service.MeasurementService;
import ru.alexsumin.weightstatbot.util.UserChoiceParser;

@Component
public class CommandFactory {

    private final AccountService accountService;
    private final MeasurementService measurementService;

    public CommandFactory(AccountService accountService, MeasurementService measurementService) {
        this.accountService = accountService;
        this.measurementService = measurementService;
    }

    public Command getCommand(Message message) {
        String text = message.getText().toLowerCase();
        UserAnswer answer = new UserChoiceParser(text).getUserAnswer();

        switch (answer) {
            case START:
                return new StartCommand(message);
            case GET_HELP:
                return new HelpCommand(message);
            case ADD_VALUE:
                return new AddNewValueCommand(message, accountService, measurementService);
            case GET_STAT:
                return new StatCommand(message, measurementService);
            case GET_CHART:
                return new ChartCommand(message, measurementService);
            case DELETE:
                return new DeleteCommand(message, measurementService);
            default:
                return new UnknownCommand(message);

        }
    }
}
