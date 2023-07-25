package com.example.backend.services;

import com.example.backend.dao.CustomerDAO;
import com.example.backend.dao.RealtyObjectDAO;
import com.example.backend.models.Customer;
import com.example.backend.models.Price;
import com.example.backend.models.enums.Real_Estate;
import com.example.backend.models.Realty_Object;
import com.example.backend.models.enums.Type_Of_Order_Of_Real_Estate;
import jakarta.validation.*;
import lombok.AllArgsConstructor;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.regex.Pattern;

@Service
@Validated
@AllArgsConstructor
public class RealtyObjectService {
    @Autowired
    private RealtyObjectDAO realtyObjectDAO;
    private CustomerDAO customerDAO;

    public ResponseEntity<List<Map<Integer, Realty_Object>>> getSelectedRealtyObjects(String selected,String input){
        System.out.println(selected);
        System.out.println(input);
        String[] realEstateAndTypeOfRealtyObject=selected.split(":",2);
        List<String> listRealEstateAndTypeOfRealtyObject=new ArrayList<>(List.of(realEstateAndTypeOfRealtyObject));
        System.out.println(listRealEstateAndTypeOfRealtyObject.get(0));
        List<Realty_Object> realty_objectsWithSelectedTypeOfRealEstate=new ArrayList<>(realtyObjectDAO.getRealty_ObjectByReal_estate(Real_Estate.valueOf(listRealEstateAndTypeOfRealtyObject.get(0))));
        System.out.println(realty_objectsWithSelectedTypeOfRealEstate);
        System.out.println(realty_objectsWithSelectedTypeOfRealEstate.get(0));
        List<Map<Integer,Realty_Object>> listOfCustomerIdAndRealtyObject=new ArrayList<>();
        for(Realty_Object realty_object:realty_objectsWithSelectedTypeOfRealEstate){
            if(realty_object.getPrice().getType_of_order_of_real_estate() == Type_Of_Order_Of_Real_Estate.valueOf(listRealEstateAndTypeOfRealtyObject.get(1))){
                System.out.println(realty_object);
                List<Customer> allCustomers=customerDAO.findAll();
                for (Customer customer:allCustomers){
                    List<Realty_Object> customerRealtyObjects=customer.getMy_realty_objectList();
                    for(Realty_Object realty:customerRealtyObjects){
                        if(realty.getId() == realty_object.getId()){
                            if(Pattern.compile(Pattern.quote(realty_object.getAddress()),Pattern.CASE_INSENSITIVE).matcher(input).find()) {
                                Map<Integer, Realty_Object> mapOfCustomerIdAndRealtyObject = new HashMap<>();
                                mapOfCustomerIdAndRealtyObject.put(customer.getId(), realty_object);
                                listOfCustomerIdAndRealtyObject.add(0,mapOfCustomerIdAndRealtyObject);
                            }else {
                                Map<Integer, Realty_Object> mapOfCustomerIdAndRealtyObject = new HashMap<>();
                                mapOfCustomerIdAndRealtyObject.put(customer.getId(), realty_object);
                                listOfCustomerIdAndRealtyObject.add(mapOfCustomerIdAndRealtyObject);
                            }
                        }
                    }
                }
            }
        }
        return new ResponseEntity<>(listOfCustomerIdAndRealtyObject, HttpStatus.OK);
    }

    public ResponseEntity<?> get12RandomRealtyObject(){
        List<Realty_Object> list=realtyObjectDAO.findAll();
        if(list.size()<12){
            return new ResponseEntity<>("No objects",HttpStatus.NOT_FOUND);
        }else {
            Collections.shuffle(list);
            int numberOfElements=12;
            List<Realty_Object> newList=list.subList(0,numberOfElements);
            return new ResponseEntity<>(newList,HttpStatus.OK);
        }
    }
    public List<String> validateRealtyObject(@Valid Realty_Object realtyObject,@Valid Price realtyObjectPrice) {
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        jakarta.validation.Validator validator = factory.getValidator();
        Set<ConstraintViolation<Realty_Object>> violations = validator.validate(realtyObject);
        Set<ConstraintViolation<Price>> violationsPrice = validator.validate(realtyObjectPrice);

        if (!violations.isEmpty() || !violationsPrice.isEmpty()) {
            // Обробка помилок валідації
            // Поверніть відповідну відповідь або викличте виключення
            System.out.println("errors");
            System.out.println(violations);
            List<String> messagesErrorList = new ArrayList<>();

            for (ConstraintViolation<Realty_Object> violation : violations) {
                String propertyPath = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                System.out.println("Validation error: " + propertyPath + " - " + message);
                messagesErrorList.add(message);
            }
            for (ConstraintViolation<Price> violationPrice : violationsPrice) {
                String propertyPathPrice = violationPrice.getPropertyPath().toString();
                String messagePrice = violationPrice.getMessage();
                System.out.println("Validation error: " + propertyPathPrice + " - " + messagePrice);
                messagesErrorList.add(messagePrice);
            }
            return messagesErrorList;

        } else {
            List<String> noError = new ArrayList<>();
            noError.add("noErrors");
            System.out.println("noErrors");
            return noError;
        }
    }
}
