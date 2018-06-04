package ru.alexsumin.weightstatbot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.alexsumin.weightstatbot.domain.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {


}
