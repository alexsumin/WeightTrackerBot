package ru.alexsumin.weightstatbot.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Data
@NoArgsConstructor
@ToString(exclude = "account")
@Entity
@EqualsAndHashCode(exclude = "account")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurement_id")
    private Long id;

    private BigDecimal amount;

    private Date dateOfMeasurement;

    @ManyToOne
    private Account account;

    public Measurement(BigDecimal amount, Account account) {
        this.amount = amount;
        this.account = account;
        this.dateOfMeasurement = new Date(System.currentTimeMillis());
    }

}
