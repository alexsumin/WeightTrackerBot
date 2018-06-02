package ru.alexsumin.weightstatbot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.alexsumin.weightstatbot.model.Measurement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;


public interface MeasurementRepository extends CrudRepository<Measurement, Long> {

    @Query(value = "SELECT m.VALUE_ID  FROM measurement as m " +
            "LEFT OUTER JOIN account as a ON  m.ACCOUNT_ACCOUNT_ID = a.ACCOUNT_ID " +
            "WHERE a.CHAT_ID = :id ORDER BY m.VALUE_ID DESC LIMIT 1 ;", nativeQuery = true)
    Optional<BigInteger> getUsersLastMeasurementId(@Param("id") Long chatId);


    @Query(value = "SELECT m.VALUE_ID  FROM measurement as m " +
            "LEFT OUTER JOIN account as a ON  m.ACCOUNT_ACCOUNT_ID = a.ACCOUNT_ID " +
            "WHERE a.CHAT_ID = :id ORDER BY m.VALUE_ID LIMIT 1 ;", nativeQuery = true)
    Optional<BigInteger> getUsersFirstMeasurementId(@Param("id") Long chatId);


    @Query(value = "SELECT  m.amount  FROM measurement as m" +
            " JOIN account as a ON  m.ACCOUNT_ACCOUNT_ID = a.ACCOUNT_ID" +
            "             WHERE a.chat_id=:id AND m.DATE_OF_MEASUREMENT > DATE_ADD(current_date, INTERVAL -:period DAY) LIMIT 1; ", nativeQuery = true)
    Optional<BigDecimal> getFirstMeasurementValueInPeriod(@Param("id") Long chatId, @Param("period") int period);


    @Transactional
    @Modifying
    @Query("DELETE FROM Measurement m WHERE m.id =:id")
    void deleteById(@Param("id") Long id);


}
