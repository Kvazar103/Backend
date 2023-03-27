package com.example.backend.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
//import jakarta.persistence.*;



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
//    @OneToOne
//    @JoinColumn(name = "realty_object_id",referencedColumnName = "id")
//    private Realty_Object realty_object;
//    (cascade = CascadeType.ALL,fetch = FetchType.EAGER)


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
