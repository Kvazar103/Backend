package com.example.backend.models.dto;

import com.example.backend.models.enums.District;
import com.example.backend.models.Price;
import com.example.backend.models.enums.Real_Estate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
