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

    private void registerUser(Long chatId) {
        Account newUser = new Account(chatId);
        accountRepository.save(newUser);
        //logger.info("Register an user with chatId: " + chatId);
    }

    public Account findById(Long chatId) {
        return accountRepository.findById(chatId).get();
    }


}
