package ru.vasilyev.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ExchangeRate {
    private int id;

    @NonNull
    private Currency baseCurrencyId;

    @NonNull
    private Currency targetCurrencyId;

    @NonNull
    private BigDecimal rate;

    public ExchangeRate(@NonNull Currency baseCurrencyId, @NonNull Currency targetCurrencyId, @NonNull BigDecimal rate) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }
}