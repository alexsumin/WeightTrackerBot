package ru.alexsumin.weightstatbot.service;

import org.springframework.stereotype.Service;
import ru.alexsumin.weightstatbot.domain.Measurement;
import ru.alexsumin.weightstatbot.repository.MeasurementRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Service
public class MeasurementServiceImpl implements MeasurementService {

    private final MeasurementRepository measurementRepository;

    public MeasurementServiceImpl(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public void addNewValue(Measurement measurement) {
        measurementRepository.save(measurement);
    }

    @Override
    public Optional<BigDecimal> getUserFirstMeasurementValue(Long chatId) {
        Optional<BigInteger> firstMeasurementId = measurementRepository.getUsersFirstMeasurementId(chatId);
        if (firstMeasurementId.isPresent()) {
            return Optional.of(measurementRepository.findById(firstMeasurementId.get().longValue()).get().getAmount());
        }
        return Optional.empty();
    }

    @Override
    public Optional<BigDecimal> getFirstMeasurementValueInPeriod(Long chatId, int period) {
        return measurementRepository.getFirstMeasurementValueInPeriod(chatId, period);
    }

    @Override
    public boolean deleteLastMeasurement(Long chatId) {
        Optional<BigInteger> measurement = measurementRepository.getUsersLastMeasurementId(chatId);

        if (!measurement.isPresent())
            return false;

        Long lastMeasurement = measurement.get().longValue();
        measurementRepository.deleteById(lastMeasurement);
        return true;
    }

    @Override
    public Optional<BigDecimal> getUserLastMeasurementValue(Long chatId) {
        Optional<BigInteger> lastMeasurementId;
        lastMeasurementId = measurementRepository.getUsersLastMeasurementId(chatId);

        if (lastMeasurementId.isPresent()) {
            return Optional.of(measurementRepository.findById(lastMeasurementId.get().longValue())
                    .get().getAmount());
        }
        return Optional.empty();
    }

}
