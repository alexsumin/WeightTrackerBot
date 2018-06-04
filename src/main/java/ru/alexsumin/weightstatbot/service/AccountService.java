package ru.alexsumin.weightstatbot.service;

import ru.alexsumin.weightstatbot.domain.Account;

public interface AccountService {

    boolean isUserExists(Long chatId);

    Account findById(Long chatId);

}
