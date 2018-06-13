package ru.alexsumin.weightstatbot.service;

import org.springframework.stereotype.Service;
import ru.alexsumin.weightstatbot.domain.Account;
import ru.alexsumin.weightstatbot.repository.AccountRepository;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean isUserExists(Long chatId) {
        Optional<Account> user = accountRepository.findById(chatId);
        if (user.isPresent())
            return true;
        else registerUser(chatId);
        return false;
    }

    private Account registerUser(Long chatId) {
        Account newUser = new Account(chatId);
        accountRepository.save(newUser);
        return newUser;
    }

    public Account findById(Long chatId) {
        Optional<Account> account = accountRepository.findById(chatId);
        return account.orElseGet(() -> registerUser(chatId));

    }


}
