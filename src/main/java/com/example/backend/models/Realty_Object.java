package com.example.backend.models;

import com.example.backend.models.enums.District;
import com.example.backend.models.enums.Real_Estate;
import lombok.*;


import javax.persistence.*;
//import javax.validation.constraints.Min;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
import java.util.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

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
    @NotNull(message = "District is required")
    @Enumerated(EnumType.STRING)
    private District district;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Apt/Suite/Building is required")
    private String apt_suite_building;
    @NotNull(message = "Rooms are required")
    @Min(value = 1, message = "Rooms must be a positive number")
    private Integer rooms;
    @NotNull(message = "Square is required")
    @Min(value = 1, message = "Square must be a positive number")
    private int square;
    @NotBlank(message = "Details are required")
    private String details;

    private final Date creationDate = new Date();
    private String DateOfCreation;

    private Date updateDate=new Date();
    private String dateOfUpdate;
//    @ElementCollection
    @ElementCollection(fetch = EAGER)
    private List<String> images=new ArrayList<>();
    @NotNull(message = "Real estate is required")
    @Enumerated(EnumType.STRING)
    private Real_Estate real_estate;
    @OneToOne(cascade = CascadeType.ALL,fetch = EAGER) ////cascadetype.all при видалені realty_object видаляється і price
    @JoinColumn(name="price_id",referencedColumnName = "id")
    private Price price;

    public Realty_Object(District district, String address, String apt_suite_building, int rooms, int square, String details, List<String> images, Real_Estate real_estate, Price price) {

    }

    public Realty_Object(Realty_Object realtyObject) {

    }

    //    public String getDateOfCreation() {
//        return DateOfCreation;
//    }
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
