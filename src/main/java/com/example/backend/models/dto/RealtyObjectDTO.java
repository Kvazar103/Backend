package com.example.backend.models.dto;

import com.example.backend.models.District;
import com.example.backend.models.Price;
import com.example.backend.models.Real_Estate;
import com.example.backend.models.Realty_Object;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RealtyObjectDTO {
    private int id;
    private final String city="Lviv";
    private District district;
    private String address;
    private String apt_suite_building;
    private int rooms;
    private int square;
    private String details;
    private List<String> images=new ArrayList<>();
    private Real_Estate real_estate;
    private Price price;

}
