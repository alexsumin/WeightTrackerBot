package ru.alexsumin.weightstatbot.service;

import ru.alexsumin.weightstatbot.model.Account;

public interface AccountService {

    boolean isUserExists(Long chatId);

    Account findByChatId(Long chatId);

}
