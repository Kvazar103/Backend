package com.example.backend.services;


import com.example.backend.dao.CustomerDAO;
import com.example.backend.dao.RealtyObjectDAO;
import com.example.backend.models.Customer;
import com.example.backend.models.Price;
import com.example.backend.models.Realty_Object;
import com.example.backend.models.dto.CustomerNoPasswordDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.util.*;

@Service
@AllArgsConstructor
@EnableWebSecurity //для роботи метода ifPasswordMatchesSave
public class CustomerService {
    private CustomerDAO customerDAO;
    private RealtyObjectDAO realtyObjectDAO;

    private PasswordEncoder passwordEncoder;

    BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    public ResponseEntity<String> ifPasswordMatchesSave(Integer customerID, String oldPassword, String newPassword){
        Customer customerToCheck=customerDAO.findCustomerById(customerID);

        boolean isPasswordMatches = bcrypt.matches(oldPassword,customerToCheck.getPassword());
        if(isPasswordMatches){
            customerToCheck.setPassword(passwordEncoder.encode(newPassword));
            customerDAO.save(customerToCheck);
            return new ResponseEntity<>("Password true", HttpStatus.OK);
        }else {
            System.out.println("wrong");
            return new ResponseEntity<>("Password false",HttpStatus.UNAUTHORIZED);
        }
    }
    public void addRealtyObjectToCustomerAddedToFavoriteList(Integer id,String realty_object){
        Customer customerToUpdate =  customerDAO.findCustomerById(id);

        System.out.println(customerToUpdate);
        System.out.println(realty_object);

        ObjectMapper mapper=new ObjectMapper();

        try {
            Realty_Object object=mapper.readValue(realty_object,Realty_Object.class);
            System.out.println(object);
//            Realty_Object obj=new Realty_Object()
            List<Integer> customerFavoriteList =  customerToUpdate.getAdded_to_favorites();
            customerFavoriteList.add(object.getId());
            System.out.println(customerFavoriteList);
            System.out.println(customerToUpdate);
            customerDAO.save(customerToUpdate);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteRealtyObjectFromCustomerAddedToFavoriteList(Integer customerId,Integer realtyObjectToDeleteId){
        Customer customer=customerDAO.findCustomerById(customerId);
        System.out.println(customer);
        List<Integer> addedToFavorite=customer.getAdded_to_favorites();
        for (int i=0;i<addedToFavorite.size();i++){
            if(addedToFavorite.get(i).equals(realtyObjectToDeleteId)){
                addedToFavorite.remove(addedToFavorite.get(i));
                customer.setAdded_to_favorites(addedToFavorite);
                customerDAO.save(customer);
            }
        }
    }
    public ResponseEntity<List<Map<Integer,Realty_Object>>> getCurrentUserListOfAddedToFavorite(Integer customerId){
        Customer customer=customerDAO.findCustomerById(customerId);
        List<Integer> customerAddedToFavoriteObjectsList=customer.getAdded_to_favorites();
        List<Map<Integer,Realty_Object>> customerIdAndRealtyObject=new ArrayList<>();
        for(Integer numOfRealty:customerAddedToFavoriteObjectsList){
            Realty_Object realty_object=realtyObjectDAO.findRealty_ObjectById(numOfRealty);
            List<Customer> allCustomers=customerDAO.findAll();
            for (Customer customer1:allCustomers){
                List<Realty_Object> customer1RealtyObjectList=customer1.getMy_realty_objectList();
                for (Realty_Object realty:customer1RealtyObjectList){
                    if(realty == realty_object){
                        System.out.println(realty);
                        System.out.println(customer1);
                        Map<Integer,Realty_Object> idRealty=new HashMap<>();
                        idRealty.put(customer1.getId(),realty);
                        customerIdAndRealtyObject.add(idRealty);
                    }
                }
            }
        }
        return new ResponseEntity<>(customerIdAndRealtyObject,HttpStatus.OK);
    }
    public ResponseEntity<Customer> deleteRealtyObject(Integer customerId, Integer realtyObjectId){
        Customer customer=customerDAO.findCustomerById(customerId);
        System.out.println(customer);
        List<Realty_Object> realty_objectList= customer.getMy_realty_objectList();
        List<Integer> favoritesList=customer.getAdded_to_favorites();
        System.out.println(realty_objectList);
        for (Realty_Object el:realty_objectList) {
            if(el.getId()==realtyObjectId){
//                for (Integer id:favoritesList){
//                    if(Objects.equals(id, realtyObjectId)){
//                        favoritesList.remove(id);
//                        customer.setAdded_to_favorites(favoritesList);
//                    }
//                }
                for(int i=0;i<favoritesList.size();i++){
                    if(Objects.equals(favoritesList.get(0), realtyObjectId)){
                        favoritesList.remove(favoritesList.get(0));
                        customer.setAdded_to_favorites(favoritesList);
                    }
                }
                System.out.println(el);
                realty_objectList.remove(el);
                System.out.println(realty_objectList);
                customer.setMy_realty_objectList(realty_objectList);
                customerDAO.save(customer);
            }
        }
        return new ResponseEntity<>(customer,HttpStatus.OK);
    }
    public ResponseEntity<CustomerNoPasswordDTO> deleteCustomer(Integer customerId){
        Customer customerToDelete=customerDAO.findCustomerById(customerId);
        customerDAO.delete(customerToDelete);
        CustomerNoPasswordDTO deletedCustomerWithoutPassword=new CustomerNoPasswordDTO(customerToDelete.getId(),customerToDelete.getName(),customerToDelete.getSurname(), customerToDelete.getEmail(), customerToDelete.getLogin(), customerToDelete.getPhone_number(), customerToDelete.getAvatar(), customerToDelete.getMy_realty_objectList(),customerToDelete.getAdded_to_favorites());
        return new ResponseEntity<>(deletedCustomerWithoutPassword,HttpStatus.OK);
    }

    public ResponseEntity<List<CustomerNoPasswordDTO>> getCustomersWithoutPassword(){
        List<Customer> list=customerDAO.findAll();
        List<CustomerNoPasswordDTO> customerNoPasswordDTOList=new ArrayList<>();
        for (Customer el:list){
            CustomerNoPasswordDTO customerNoPasswordDTO=new CustomerNoPasswordDTO(el.getId(), el.getName(), el.getSurname(), el.getEmail(), el.getLogin(), el.getPhone_number(), el.getAvatar(), el.getMy_realty_objectList(),el.getAdded_to_favorites());
            customerNoPasswordDTOList.add(customerNoPasswordDTO);
        }
        return new ResponseEntity<>(customerNoPasswordDTOList,HttpStatus.OK);
    }
    public ResponseEntity<?> getCustomerById(Integer customerID){
        Customer customer=customerDAO.findCustomerById(customerID);
        if(customer!=null){
            CustomerNoPasswordDTO customerNoPasswordDTO=new CustomerNoPasswordDTO(customer.getId(),
                    customer.getName(),
                    customer.getSurname(),
                    customer.getEmail(),
                    customer.getLogin(),
                    customer.getPhone_number(),
                    customer.getAvatar(),
                    customer.getMy_realty_objectList(),
                    customer.getAdded_to_favorites());
            return new ResponseEntity<>(customerNoPasswordDTO,HttpStatus.OK);
        }else {
            return new ResponseEntity<>("no such user exists",HttpStatus.NOT_FOUND);
        }
    }
//    public ResponseEntity<Customer> getCustomerAfterUpdateWithPassword(Integer customerID){
//        Customer customer=customerDAO.findCustomerById(customerID);
//        return new ResponseEntity<>(customer,HttpStatus.OK);
//    }
//    public Customer getCustomerAfterUpdateWithPassword(Integer customerID){
//            return customerDAO.findCustomerById(customerID);
//    }
    public List<String> validateCustomer(@Valid Customer customer){
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        jakarta.validation.Validator validator = factory.getValidator();
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        if (!violations.isEmpty()) {
            // Обробка помилок валідації
            // Поверніть відповідну відповідь або викличте виключення
            System.out.println("errors");
            System.out.println(violations);
            List<String> messagesErrorList = new ArrayList<>();

            for (ConstraintViolation<Customer> violation : violations) {
                String propertyPath = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                System.out.println("Validation error: " + propertyPath + " - " + message);
                messagesErrorList.add(message);
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
