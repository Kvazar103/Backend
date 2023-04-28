package com.example.backend.models;


//import jakarta.persistence.*;
import lombok.*;


import javax.persistence.*;
import javax.swing.*;
import java.util.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Realty_Object {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private final String city="Lviv";
    @Enumerated(EnumType.STRING)
    private District district;
    private String address;
    private String apt_suite_building;
    private int rooms;
    private int square;
    private String details;


    private final Date creationDate = new Date();
    private String DateOfCreation;

    private Date updateDate=new Date();
    private String dateOfUpdate;


    @ElementCollection
    private List<String> images=new ArrayList<>();
//    private String images;
    @Enumerated(EnumType.STRING)
    private Real_Estate real_estate;
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER) ////cascadetype.all при видалені realty_object видаляється і price
    @JoinColumn(name="price_id",referencedColumnName = "id")
    private Price price;



    public String getDateOfCreation() {
        return DateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        DateOfCreation = dateOfCreation;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setDateOfUpdate(String dateOfUpdate) {
        this.dateOfUpdate = dateOfUpdate;
    }
    //    @ManyToOne
//    private Customer customer;
    //    @JoinColumn(name="realty_object_owner",referencedColumnName = "id")
//    (cascade = CascadeType.ALL,fetch = FetchType.LAZY) //many to one uni directional(багато нерухомості до одного юзера)


    @Override
    public String toString() {
        return "Realty_Object{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", district=" + district +
                ", address='" + address + '\'' +
                ", apt_suite_building='" + apt_suite_building + '\'' +
                ", rooms=" + rooms +
                ", square=" + square +
                ", details='" + details + '\'' +
                ", creationDate=" + creationDate +
                ", DateOfCreation='" + DateOfCreation + '\'' +
                ", images=" + images +
                ", real_estate=" + real_estate +
                ", price=" + price +
                '}';
    }
}
