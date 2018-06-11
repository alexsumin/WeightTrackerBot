package ru.alexsumin.weightstatbot.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import ru.alexsumin.weightstatbot.service.MeasurementService;

public class DeleteCommand extends Command {

    private static final String SUCCESS = "The last measurement was succesfully deleted!";
    private static final String NO_VALUES = "Oops! No measurements were found. Just add the new one to start!";

    private final Message message;
    private final MeasurementService measurementService;

    public DeleteCommand(Message message, MeasurementService measurementService) {
        this.message = message;
        this.measurementService = measurementService;
    }

    @Override
    public CommandResponse call() throws Exception {
        Long chatId = message.getChatId();

        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(chatId);

        if (!measurementService.deleteLastMeasurement(chatId))
            responseMessage.setText(NO_VALUES);
        else {
            responseMessage.setText(SUCCESS);
        }

        return new CommandResponse(responseMessage);
    }
}
