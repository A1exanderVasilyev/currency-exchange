package ru.vasilyev.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
public class ExchangeRate {
    private int id;

    @NonNull
    private Currency baseCurrency;

    @NonNull
    private Currency targetCurrency;

    @NonNull
    private BigDecimal rate;

    public ExchangeRate(@NonNull Currency baseCurrency, @NonNull Currency targetCurrency, @NonNull BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public @NonNull BigDecimal getRate() {
        return rate.setScale(2, RoundingMode.HALF_UP);
    }
}