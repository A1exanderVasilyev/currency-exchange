package ru.vasilyev.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Currency {
    private int id;

    @NonNull
    private String code;

    @NonNull
    private String fullName;

    @NonNull
    private String sign;

    public Currency(@NonNull String code, @NonNull String fullName, @NonNull String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
