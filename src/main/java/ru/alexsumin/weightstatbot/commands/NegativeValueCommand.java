package ru.alexsumin.weightstatbot.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class NegativeValueCommand extends Command {

    private static final String NEGATIVE_VALUE = "Oops! Are you sure? " +
            "The weight can not be less than zero. Please, try again";

    private final Message message;

    public NegativeValueCommand(Message message) {
        this.message = message;
    }


    @Override
    public CommandResponse call() throws Exception {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(this.message.getChatId());
        responseMessage.setText(NEGATIVE_VALUE);

        return new CommandResponse(responseMessage);
    }
}
