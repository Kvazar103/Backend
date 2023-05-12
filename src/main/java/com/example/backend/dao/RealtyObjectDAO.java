package com.example.backend.dao;

import com.example.backend.models.Real_Estate;
import com.example.backend.models.Realty_Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RealtyObjectDAO extends JpaRepository<Realty_Object,Integer> {

    Realty_Object findRealty_ObjectById(Integer id);
    void deleteRealty_ObjectsById(Integer id);
//    List<Realty_Object> findRealty_ObjectsByAddressOrCity(String msg);
    @Query("select r from Realty_Object r where r.real_estate=:real_estate")
    List<Realty_Object> getRealty_ObjectByReal_estate(Real_Estate real_estate);


}
