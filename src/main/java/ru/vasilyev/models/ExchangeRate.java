package ru.vasilyev.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}