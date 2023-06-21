package com.example.backend.models;

import com.example.backend.models.enums.Currency;
import com.example.backend.models.enums.Type_Of_Order_Of_Real_Estate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int sum;
    @Enumerated(EnumType.STRING)
    private Currency currency;
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
