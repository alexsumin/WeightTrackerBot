package ru.alexsumin.weightstatbot.service;

import ru.alexsumin.weightstatbot.domain.Measurement;

import java.math.BigDecimal;
import java.util.Optional;

public interface MeasurementService {

    void addNewValue(Measurement measurement);

    Optional<BigDecimal> getUsersLastMeasurementValue(Long chatId);

    void deleteById(Long chatId);

    Optional<BigDecimal> getUsersFirstMeasurementValue(Long chatId);

    Optional<Measurement> findById(Long id);

    Optional<BigDecimal> getFirstMeasurementValueInPeriod(Long chatId, int period);

    boolean deleteLastMeasurement(Long chatId);

}
