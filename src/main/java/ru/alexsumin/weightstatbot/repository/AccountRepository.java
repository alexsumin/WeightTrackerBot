package ru.alexsumin.weightstatbot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.alexsumin.weightstatbot.model.Account;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    @Query("select a from Account a where a.chatId = :id")
    Optional<Account> findByChatId(@Param("id") Long chatId);


}
