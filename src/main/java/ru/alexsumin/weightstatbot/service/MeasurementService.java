package ru.alexsumin.weightstatbot.service;

import ru.alexsumin.weightstatbot.domain.Measurement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MeasurementService {

    void addNewValue(Measurement measurement);

    List<Measurement> getUserAllMeasurements(Long chatId);

    Optional<BigDecimal> getUserLastMeasurementValue(Long chatId);

    Optional<BigDecimal> getUserFirstMeasurementValue(Long chatId);

    Optional<BigDecimal> getFirstMeasurementValueInPeriod(Long chatId, int period);

    boolean deleteLastMeasurement(Long chatId);

}
