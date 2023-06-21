package com.example.backend.models;

import com.example.backend.models.enums.District;
import com.example.backend.models.enums.Real_Estate;
import lombok.*;


import javax.persistence.*;
import java.util.*;

import static javax.persistence.FetchType.EAGER;


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
//    @ElementCollection
    @ElementCollection(fetch = EAGER)
    private List<String> images=new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private Real_Estate real_estate;
    @OneToOne(cascade = CascadeType.ALL,fetch = EAGER) ////cascadetype.all при видалені realty_object видаляється і price
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
