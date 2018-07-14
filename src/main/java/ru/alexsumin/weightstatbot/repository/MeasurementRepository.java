package ru.alexsumin.weightstatbot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.alexsumin.weightstatbot.domain.Measurement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


public interface MeasurementRepository extends CrudRepository<Measurement, Long> {

    @Transactional
    @Query(value = "SELECT m FROM Measurement m INNER JOIN m.account a WHERE a.id =:id")
    List<Measurement> getAllUsersMeasurement(@Param("id") Long chatId);

    @Query(value = "SELECT m.MEASUREMENT_ID  FROM measurement as m " +
            "LEFT OUTER JOIN account as a ON  m.ACCOUNT_ACCOUNT_ID = a.account_id " +
            "WHERE a.account_id = :id ORDER BY m.MEASUREMENT_ID DESC LIMIT 1 ;", nativeQuery = true)
    Optional<BigInteger> getUsersLastMeasurementId(@Param("id") Long chatId);


    @Query(value = "SELECT m.MEASUREMENT_ID  FROM measurement as m " +
            "LEFT OUTER JOIN account as a ON  m.ACCOUNT_ACCOUNT_ID = a.account_id " +
            "WHERE a.account_id = :id ORDER BY m.MEASUREMENT_ID LIMIT 1 ;", nativeQuery = true)
    Optional<BigInteger> getUsersFirstMeasurementId(@Param("id") Long chatId);


    @Query(value = "SELECT  m.amount  FROM measurement as m" +
            " JOIN account as a ON  m.ACCOUNT_ACCOUNT_ID = a.account_id " +
            "WHERE a.account_id=:id AND m.DATE_OF_MEASUREMENT > DATE_ADD(current_date, INTERVAL -:period DAY) LIMIT 1; ", nativeQuery = true)
    Optional<BigDecimal> getFirstMeasurementValueInPeriod(@Param("id") Long chatId, @Param("period") int period);

    @Transactional
    @Modifying
    @Query("DELETE FROM Measurement m WHERE m.id =:id")
    void deleteById(@Param("id") Long id);

}
