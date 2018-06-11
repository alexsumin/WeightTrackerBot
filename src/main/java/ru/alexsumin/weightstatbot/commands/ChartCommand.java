package ru.alexsumin.weightstatbot.commands;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import ru.alexsumin.weightstatbot.chart.ChartGenerator;
import ru.alexsumin.weightstatbot.domain.Account;
import ru.alexsumin.weightstatbot.domain.Measurement;
import ru.alexsumin.weightstatbot.service.AccountService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class ChartCommand extends Command {

    private static final String NO_VALUES = "Oops! No measurements were found. Just add the new one to start!";

    private final Message message;
    private final AccountService accountService;

    public ChartCommand(Message message, AccountService accountService) {
        this.message = message;
        this.accountService = accountService;
    }

    @Override
    public CommandResponse call() throws Exception {
        Long chatId = message.getChatId();
        Account account = accountService.findById(chatId);

        List<Measurement> list = account.getMeasurements();
        try {
            if (list.size() == 0) {
                SendMessage responseMessage = new SendMessage();
                responseMessage.setChatId(chatId);
                responseMessage.setText(NO_VALUES);
                return new CommandResponse(responseMessage);
            }
            byte[] file = ChartGenerator.generateChart(list);
            InputStream bis = new ByteArrayInputStream(file);

            SendPhoto photoMessage = new SendPhoto().setChatId(chatId);
            photoMessage.setNewPhoto("stat.png", bis);
            return new CommandResponse(photoMessage);


        } catch (ChartGenerator.ChartGenerationException c) {
            throw new RuntimeException("Couldn't generate a chart and send to user.chatId " + chatId + ". " + c.getMessage());
        }
    }
}
