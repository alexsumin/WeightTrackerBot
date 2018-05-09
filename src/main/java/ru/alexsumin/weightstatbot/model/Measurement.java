package ru.alexsumin.weightstatbot.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;

@Entity
public class Measurement {
    private static final SimpleDateFormat format = new SimpleDateFormat("dd:mm:yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "value_id")
    private Long id;

    private BigDecimal amount;

    private Date dateOfMeasurement;

    @ManyToOne
    private Account account;

    public Measurement() {
    }

    public Measurement(BigDecimal amount, Account account) {
        this.amount = amount;
        this.account = account;
        this.dateOfMeasurement = new Date(System.currentTimeMillis());
    }

    public Date getDateOfMeasurement() {
        return dateOfMeasurement;
    }

    public void setDateOfMeasurement(Date dateOfMeasurement) {
        this.dateOfMeasurement = dateOfMeasurement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measurement measurement = (Measurement) o;

        return id != null ? id.equals(measurement.id) : measurement.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "id=" + id +
                ", amount=" + amount +
                ", dateOfMeasurement=" + dateOfMeasurement;
    }
}
