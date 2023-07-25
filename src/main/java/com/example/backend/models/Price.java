package com.example.backend.models;

import com.example.backend.models.enums.Currency;
import com.example.backend.models.enums.Type_Of_Order_Of_Real_Estate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull(message = "Sum is required")
    @Min(value = 1, message = "Sum must be a positive number")
    private int sum;
    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @NotNull(message = "Type of order of real estate is required")
    @Enumerated(EnumType.STRING)
    private Type_Of_Order_Of_Real_Estate type_of_order_of_real_estate;

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", sum=" + sum +
                ", currency=" + currency +
                ", type_of_order_of_real_estate=" + type_of_order_of_real_estate +
                '}';
    }
}
