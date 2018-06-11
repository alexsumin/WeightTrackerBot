package ru.alexsumin.weightstatbot.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class StartCommand extends Command {


    private static final String START = "What is this bot designed for? " +
            "This simple bot helps you to monitor your weight, presenting the results as a chart. " +
            "To receive the clue just write /help. " +
            "New functions will be added soon! Have a nice day :)";

    private final Message message;

    public StartCommand(Message message) {
        this.message = message;
    }

    @Override
    public CommandResponse call() throws Exception {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(this.message.getChatId());
        responseMessage.setText(START);

        return new CommandResponse(responseMessage);
    }
}
