package ru.alexsumin.weightstatbot.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.alexsumin.weightstatbot.domain.Account;
import ru.alexsumin.weightstatbot.repository.AccountRepository;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    AccountServiceImpl accountService;

    @Mock
    AccountRepository accountRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    public void findByIdTest() {
        Account account = new Account();
        account.setId(1L);

        Optional<Account> accountOptional = Optional.of(account);


        when(accountRepository.findById(anyLong())).thenReturn(accountOptional);

        Account foundAccount = accountService.findById(1L);

        assertNotNull(foundAccount);
        verify(accountRepository, times(1)).findById(anyLong());
    }

}