package com.example.backend.models;


//import jakarta.persistence.*;
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
public class Realty_Object {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String address;
    private String distinct;
    private int rooms;
    private int square;
    @Enumerated(EnumType.STRING)
    private Real_Estate real_estate;
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER) ////cascadetype.all при видалені realty_object видаляється і price
    @JoinColumn(name="price_id",referencedColumnName = "id")
    private Price price;
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY) //many to one uni directional(багато нерухомості до одного юзера)
    @JoinColumn(name="realty_object_owner",referencedColumnName = "id")
    private Customer customer;
}
