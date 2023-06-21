package com.example.backend.dao;

import com.example.backend.models.enums.Real_Estate;
import com.example.backend.models.Realty_Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RealtyObjectDAO extends JpaRepository<Realty_Object,Integer> {
    Realty_Object findRealty_ObjectById(Integer id);
    @Query("select r from Realty_Object r where r.real_estate=:real_estate")
    List<Realty_Object> getRealty_ObjectByReal_estate(Real_Estate real_estate);
}
