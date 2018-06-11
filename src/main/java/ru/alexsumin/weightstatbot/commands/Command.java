package ru.alexsumin.weightstatbot.commands;

import java.util.concurrent.Callable;

public abstract class Command implements Callable<CommandResponse> {

    @Override
    public abstract CommandResponse call() throws Exception;
}
