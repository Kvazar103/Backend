package com.example.backend.models.dto;

import com.example.backend.models.Realty_Object;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerNoPasswordDTO {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String login;
    private long phone_number;
    private String avatar;
    private List<Realty_Object> my_realty_objectList;


    private List<Integer> added_to_favorites=new ArrayList<>();

}
