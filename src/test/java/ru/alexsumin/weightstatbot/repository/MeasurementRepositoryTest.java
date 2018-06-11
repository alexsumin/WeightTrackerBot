package ru.alexsumin.weightstatbot.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alexsumin.weightstatbot.domain.Account;
import ru.alexsumin.weightstatbot.domain.Measurement;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MeasurementRepositoryTest {

    private static final long nonRealTestId = 11111111111111L;
    @Autowired
    MeasurementRepository measurementRepository;
    @Autowired
    AccountRepository accountRepository;

    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void findByIdTest() {
        Account account = new Account(nonRealTestId);
        BigDecimal value = BigDecimal.valueOf(100);

        Measurement measurement = new Measurement();
        measurement.setAmount(value);
        measurement.setDateOfMeasurement(Date.valueOf("2018-01-10"));
        measurement.setAccount(account);

        measurementRepository.save(measurement);
        accountRepository.save(account);

        Measurement m = measurementRepository.findById(measurement.getId()).get();

        assertEquals(value, m.getAmount());

    }

    @Test
    public void getUsersFirstMeasurementIdTest() {

        Account account = new Account(nonRealTestId);

        BigDecimal value = BigDecimal.valueOf(110);

        Measurement measurement = new Measurement();
        measurement.setAmount(value);
        measurement.setDateOfMeasurement(Date.valueOf("2018-01-11"));
        measurement.setAccount(account);

        accountRepository.save(account);
        measurementRepository.save(measurement);


        BigDecimal value2 = BigDecimal.valueOf(120);

        Measurement measurement2 = new Measurement();
        measurement2.setAmount(value2);
        measurement2.setDateOfMeasurement(Date.valueOf("2018-01-12"));
        measurement2.setAccount(account);

        measurementRepository.save(measurement2);

        measurementRepository.deleteById(measurement.getId());

        Long founded = measurementRepository.getUsersFirstMeasurementId(nonRealTestId).get().longValue();

        assertEquals(measurement2.getId(), founded);

        assertEquals(measurement2.getAmount(),
                measurementRepository.findById(founded).get().getAmount());

    }

    @Test
    public void getUsersLastMeasurementIdTest() {
        Account account = new Account(nonRealTestId);

        BigDecimal value = BigDecimal.valueOf(110);

        Measurement measurement = new Measurement(value, account);

        accountRepository.save(account);
        measurementRepository.save(measurement);


        BigDecimal valueSecond = BigDecimal.valueOf(120);
        Measurement measurementSecond = new Measurement(valueSecond, account);
        measurementRepository.save(measurementSecond);

        measurementRepository.deleteById(measurementSecond.getId());

        Long founded = measurementRepository.getUsersLastMeasurementId(nonRealTestId).get().longValue();

        assertEquals(measurement.getId(), founded);
        assertEquals(measurement.getAmount(),
                measurementRepository.findById(founded).get().getAmount());
    }


    @Test(expected = NoSuchElementException.class)
    public void deleteByIdTest() {
        Account account = new Account();
        account.setId(nonRealTestId);

        Measurement measurement = new Measurement(BigDecimal.valueOf(75.55), account);

        accountRepository.save(account);
        measurementRepository.save(measurement);

        Long idToDelete = 5L;
        Measurement measurementSecond = new Measurement(BigDecimal.valueOf(120), account);
        measurementSecond.setId(idToDelete);
        measurementRepository.save(measurementSecond);



        measurementRepository.deleteById(idToDelete);
        Measurement afterDelete = measurementRepository.findById(idToDelete).get();
        assertEquals(idToDelete, afterDelete.getId());
    }


}

