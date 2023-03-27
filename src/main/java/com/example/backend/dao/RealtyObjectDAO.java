package com.example.backend.dao;

import com.example.backend.models.Realty_Object;
import org.springframework.data.jpa.repository.JpaRepository;



public interface RealtyObjectDAO extends JpaRepository<Realty_Object,Integer> {

    Realty_Object findRealty_ObjectById(Integer id);

    void deleteRealty_ObjectsById(Integer id);
}
