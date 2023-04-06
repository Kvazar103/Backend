package com.example.backend.models;


import lombok.*;

//import jakarta.persistence.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String surname;
//    @Email(message = "Email is not valid", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]" +
//            "+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-" +
//            "\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\" +
//            "[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:" +
//            "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
//    @NotEmpty(message = "Email cannot be empty")
    private String email;
//    @Column(unique = true)
    private String login;
    private String password;
    private long phone_number;
    private String avatar;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinTable(name = "user_realtyObject",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_realty_object_id"))
    private List<Realty_Object> my_realty_objectList;
//    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    @JoinTable(name="user_added_to_favorite",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "added_to_favorite_id"))
//    private List<Realty_Object> added_to_favorites=new ArrayList<>();
//     @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//     @JoinTable(name="user_added_to_favorite",
//              joinColumns = @JoinColumn(name = "user_id"),
//              inverseJoinColumns = @JoinColumn(name = "added_to_favorite_id"))
    @ElementCollection
    private List<Integer> added_to_favorites=new ArrayList<>();

    public Customer(String name, String surname) {

    }


    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", phone_number=" + phone_number +
                ", my_realty_objectList=" + my_realty_objectList +
                ", added_to_favorites=" + added_to_favorites +
                '}';
    }

}
