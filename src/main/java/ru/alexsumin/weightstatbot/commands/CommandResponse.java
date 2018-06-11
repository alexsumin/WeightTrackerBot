package ru.alexsumin.weightstatbot.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;

public class CommandResponse {

    private final SendMessage sendMessage;
    private final SendPhoto sendPhoto;

    public CommandResponse(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
        sendPhoto = null;
    }

    public CommandResponse(SendPhoto sendPhoto) {
        this.sendPhoto = sendPhoto;
        sendMessage = null;
    }

    public boolean isPhoto(){
        return sendPhoto != null;
    }

    public SendMessage getSendMessage() {
        return sendMessage;
    }

    public SendPhoto getSendPhoto() {
        return sendPhoto;
    }
}
