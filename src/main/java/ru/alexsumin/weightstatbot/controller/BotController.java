package ru.alexsumin.weightstatbot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.alexsumin.weightstatbot.chart.ChartGenerator;
import ru.alexsumin.weightstatbot.commands.Command;
import ru.alexsumin.weightstatbot.commands.CommandResponse;
import ru.alexsumin.weightstatbot.factory.CommandFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

@Component
public class BotController extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(BotController.class);

    private final ThreadPoolTaskExecutor taskExecutor;
    private final CommandFactory commandFactory;

    @Value("${bot.token}")
    private String token;
    @Value("${bot.username}")
    private String username;

    @Autowired
    public BotController(ThreadPoolTaskExecutor taskExecutor, CommandFactory commandFactory) {
        this.taskExecutor = taskExecutor;
        this.commandFactory = commandFactory;
    }

    @PostConstruct
    private void initChartGenerator() {
        ChartGenerator.initialize(taskExecutor);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            Message message = update.getMessage();
            Command command = commandFactory.getCommand(message);

            CompletableFuture.supplyAsync(() -> {
                try {
                    return command.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, taskExecutor)
                    .thenAcceptAsync(this::sendResponse, taskExecutor)
                    .exceptionally(throwable -> {
                        logger.error(throwable.getMessage());
                        return null;
                    });


        }
    }

    private synchronized void sendResponse(CommandResponse response) {
        try {
            if (response.isPhoto())
                sendPhoto(response.getSendPhoto());
            else execute(response.getSendMessage());
        } catch (TelegramApiException e) {
            logger.error("Couldn't send message to user " + e.getMessage());
        }

    }


    @Override
    public String getBotUsername() {
        return username;
    }


    @Override
    public String getBotToken() {
        return token;
    }
}