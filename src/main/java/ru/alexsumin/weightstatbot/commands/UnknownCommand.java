package ru.alexsumin.weightstatbot.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class  UnknownCommand extends Command {

    private final Message message;

    public UnknownCommand(Message message) {
        this.message = message;
    }

    private static final String text = "Oops! I'm sorry, but I'm not smart guy, so I don't quite understand you. " +
            "I can only recognize a few commands. Send /help to see them.";


    @Override
    public CommandResponse call() throws Exception {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId());
        responseMessage.setText(text);

        return new CommandResponse(responseMessage);
    }
}
