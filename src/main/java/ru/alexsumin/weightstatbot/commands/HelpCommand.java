package ru.alexsumin.weightstatbot.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class HelpCommand extends Command {

    private static final String HELP = "Let me explain to you how I work. " +
            "I keep your weight statistics. " +
            "To add a new measurement, simply send me this value. " +
            "To view a chart, send me /chart. " +
            "To get statistics, send me /stat." +
            "To delete the last data value, send me /delete.";

    private final Message message;

    public HelpCommand(Message message) {
        this.message = message;
    }

    @Override
    public CommandResponse call() throws Exception {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(this.message.getChatId());
        responseMessage.setText(HELP);

        return new CommandResponse(responseMessage);
    }
}
