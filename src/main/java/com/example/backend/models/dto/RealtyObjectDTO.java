package com.example.backend.models.dto;

import com.example.backend.models.Price;
import com.example.backend.models.Real_Estate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RealtyObjectDTO {
    private String address;
    private String distinct;
    private int rooms;
    private int square;
//    private List<String> images=new ArrayList<>();
    private Real_Estate real_estate;
    private Price price;

}
