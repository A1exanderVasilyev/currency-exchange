package ru.vasilyev.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "code", "sign"})
public class Currency {
    private int id;

    @NonNull
    @JsonProperty("name")
    private String fullName;

    @NonNull
    private String code;

    @NonNull
    private String sign;

    public Currency(@NonNull String code, @NonNull String fullName, @NonNull String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
