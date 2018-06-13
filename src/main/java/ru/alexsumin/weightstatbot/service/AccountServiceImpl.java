package ru.alexsumin.weightstatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alexsumin.weightstatbot.domain.Account;
import ru.alexsumin.weightstatbot.repository.AccountRepository;

import java.util.Optional;
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private Account registerUser(Long chatId) {
        Account newUser = new Account(chatId);
        accountRepository.save(newUser);
        log.info("Register user " + chatId);
        return newUser;
    }

    public Account findById(Long chatId) {
        Optional<Account> account = accountRepository.findById(chatId);
        return account.orElseGet(() -> registerUser(chatId));

    }


}
