package ru.alexsumin.weightstatbot.service;

import ru.alexsumin.weightstatbot.domain.Account;

public interface AccountService {

    Account findById(Long chatId);

}
