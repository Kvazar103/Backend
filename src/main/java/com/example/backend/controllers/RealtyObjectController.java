package com.example.backend.controllers;


import com.example.backend.dao.CustomerDAO;
import com.example.backend.dao.RealtyObjectDAO;
import com.example.backend.models.Customer;
import com.example.backend.models.Realty_Object;
import com.example.backend.services.CustomerService;
import com.example.backend.services.RealtyObjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@AllArgsConstructor
@CrossOrigin("http://localhost:3000")
public class RealtyObjectController {
    private RealtyObjectService realtyObjectService;
    private RealtyObjectDAO realtyObjectDAO;
    private CustomerDAO customerDAO;
    private CustomerService customerService;
    private AuthenticationManager authenticationManager;//базовий об'єкт який займається процесом аутентифікації
    private PasswordEncoder passwordEncoder;
    BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @PatchMapping("/{id}/{userId}/updateRealtyObject")
    public void updateRealtyObject(@PathVariable int id, @PathVariable int userId, @RequestParam("realty_object") String realtyObject, @RequestParam(value = "images_to_add",required = false) MultipartFile[] imagesToAdd, @RequestParam(value = "currentImages_to_delete",required = false) String[] imagesToDelete) throws IOException {
        System.out.println("controller start");
        System.out.println(realtyObject);
        System.out.println(id);
        System.out.println(userId);
        System.out.println(Arrays.toString(imagesToAdd));
        System.out.println(Arrays.toString(imagesToDelete));
        System.out.println("controller end");

        Realty_Object realty_objectToUpdate=realtyObjectDAO.findRealty_ObjectById(id);
        System.out.println(realty_objectToUpdate);

        SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");

        ObjectMapper mapper=new ObjectMapper();
        if(imagesToAdd == null && imagesToDelete==null){
            try {
                Realty_Object object=mapper.readValue(realtyObject,Realty_Object.class);
                System.out.println("no images");
                realty_objectToUpdate.setDistrict(object.getDistrict());
                realty_objectToUpdate.setAddress(object.getAddress());
                realty_objectToUpdate.setApt_suite_building(object.getApt_suite_building());
                realty_objectToUpdate.setRooms(object.getRooms());
                realty_objectToUpdate.setSquare(object.getSquare());
                realty_objectToUpdate.setDetails(object.getDetails());
                realty_objectToUpdate.setReal_estate(object.getReal_estate());
//                realty_objectToUpdate.setPrice(object.getPrice());
                realty_objectToUpdate.setDateOfUpdate(formater.format(object.getUpdateDate()));

                realty_objectToUpdate.getPrice().setCurrency(object.getPrice().getCurrency());
                realty_objectToUpdate.getPrice().setSum(object.getPrice().getSum());
                realty_objectToUpdate.getPrice().setType_of_order_of_real_estate(object.getPrice().getType_of_order_of_real_estate());

                System.out.println(realty_objectToUpdate);
                realtyObjectDAO.save(realty_objectToUpdate);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else if (imagesToAdd!=null && imagesToDelete!=null) {
            System.out.println("New images and some current images need to delete");
            try {
                Realty_Object object=mapper.readValue(realtyObject,Realty_Object.class);
                System.out.println("new images and delete images");
                realty_objectToUpdate.setDistrict(object.getDistrict());
                realty_objectToUpdate.setAddress(object.getAddress());
                realty_objectToUpdate.setApt_suite_building(object.getApt_suite_building());
                realty_objectToUpdate.setRooms(object.getRooms());
                realty_objectToUpdate.setSquare(object.getSquare());
                realty_objectToUpdate.setDetails(object.getDetails());
                realty_objectToUpdate.setReal_estate(object.getReal_estate());
//                realty_objectToUpdate.setPrice(object.getPrice());
                realty_objectToUpdate.setDateOfUpdate(formater.format(object.getUpdateDate()));

                realty_objectToUpdate.getPrice().setCurrency(object.getPrice().getCurrency());
                realty_objectToUpdate.getPrice().setSum(object.getPrice().getSum());
                realty_objectToUpdate.getPrice().setType_of_order_of_real_estate(object.getPrice().getType_of_order_of_real_estate());

                String uId=userId+"id";
                System.out.println(uId);
                String home = System.getProperty("user.home");
                String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                        File.separator+"images"+File.separator+uId+File.separator;
                System.out.println(Arrays.toString(imagesToAdd));

                List<String> currentRealtyImages=realty_objectToUpdate.getImages();
                Arrays.asList(imagesToAdd).stream().forEach(multipartFile -> {
                    try {
                        multipartFile.transferTo(new File(path+multipartFile.getOriginalFilename()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    currentRealtyImages.add(multipartFile.getOriginalFilename());
                    System.out.println(currentRealtyImages);
                });

                System.out.println(realty_objectToUpdate);
//                realtyObjectDAO.save(realty_objectToUpdate);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            String home = System.getProperty("user.home");
            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator;
            System.out.println(Arrays.toString(imagesToDelete));
//            String imagesToDeletePathArray = Arrays.toString(imagesToDelete);
            List<String> imagesToDeletePathArray = new ArrayList<>(List.of(imagesToDelete));
            System.out.println(imagesToDeletePathArray);
            List<String> imagesNames=new ArrayList<>();
            for (String pat: imagesToDeletePathArray){
                System.out.println(Arrays.toString(pat.split("/", 6)));
                List<String> splittedPath= List.of(pat.split("/", 6));
                System.out.println(splittedPath);
                System.out.println(splittedPath.get(splittedPath.size()-1));
                imagesNames.add(splittedPath.get(splittedPath.size()-1));
                System.out.println("cycle");
                System.out.println(imagesNames);
            }

            List<String> imagesInRealty = new ArrayList<>(realty_objectToUpdate.getImages());
            System.out.println(imagesInRealty);
            System.out.println(imagesNames);

            for(int i=0;i<imagesNames.size();i++){
                for (int t=0;t<imagesInRealty.size();t++){
                    if(Objects.equals(imagesNames.get(i), imagesInRealty.get(t))){
                        System.out.println(imagesNames.get(i));
                        System.out.println(imagesInRealty.get(t));
                        imagesInRealty.remove(imagesInRealty.get(t));
                    }
                }
            }
            realty_objectToUpdate.setImages(imagesInRealty);

            System.out.println(imagesInRealty);

            System.out.println(realty_objectToUpdate);
            realtyObjectDAO.save(realty_objectToUpdate);

            for(int i=0;i<imagesToDelete.length;i++){
                String pathToFileToDlt=imagesToDelete[i];
                System.out.println(pathToFileToDlt);
                List<String> aOfStro= List.of(pathToFileToDlt.split("/", 4));
                System.out.println(aOfStro);
                String spPthToFile=aOfStro.get(aOfStro.size()-1);
                System.out.println(spPthToFile);
                String fileDirectoryName = path.concat(spPthToFile);
                try {
                    Files.delete(Path.of(fileDirectoryName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } else if (imagesToAdd==null && imagesToDelete!=null) {
            System.out.println("There are no new images but need to delete current images");

            String home = System.getProperty("user.home");
            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator;
            System.out.println(Arrays.toString(imagesToDelete));
            List<String> imagesToDeletePathArray = new ArrayList<>(List.of(imagesToDelete));
            System.out.println(imagesToDeletePathArray);
            List<String> imagesNames=new ArrayList<>();
            for (String pat: imagesToDeletePathArray){
                System.out.println(Arrays.toString(pat.split("/", 6)));
                List<String> splittedPath= List.of(pat.split("/", 6));
                System.out.println(splittedPath);
                System.out.println(splittedPath.get(splittedPath.size()-1));
                imagesNames.add(splittedPath.get(splittedPath.size()-1));
                System.out.println("cycle");
                System.out.println(imagesNames);
            }

            try {
                Realty_Object object=mapper.readValue(realtyObject,Realty_Object.class);
                realty_objectToUpdate.setDistrict(object.getDistrict());
                realty_objectToUpdate.setAddress(object.getAddress());
                realty_objectToUpdate.setApt_suite_building(object.getApt_suite_building());
                realty_objectToUpdate.setRooms(object.getRooms());
                realty_objectToUpdate.setSquare(object.getSquare());
                realty_objectToUpdate.setDetails(object.getDetails());
                realty_objectToUpdate.setReal_estate(object.getReal_estate());
//                realty_objectToUpdate.setPrice(object.getPrice());
                realty_objectToUpdate.setDateOfUpdate(formater.format(object.getUpdateDate()));

                realty_objectToUpdate.getPrice().setCurrency(object.getPrice().getCurrency());
                realty_objectToUpdate.getPrice().setSum(object.getPrice().getSum());
                realty_objectToUpdate.getPrice().setType_of_order_of_real_estate(object.getPrice().getType_of_order_of_real_estate());

                List<String> imagesInRealty = new ArrayList<>(realty_objectToUpdate.getImages());
                System.out.println(imagesInRealty);
                System.out.println(imagesNames);

                for(int i=0;i<imagesNames.size();i++){
                    for (int t=0;t<imagesInRealty.size();t++){
                        if(Objects.equals(imagesNames.get(i), imagesInRealty.get(t))){
                            System.out.println(imagesNames.get(i));
                            System.out.println(imagesInRealty.get(t));
                            imagesInRealty.remove(imagesInRealty.get(t));
                        }
                    }
                }
                realty_objectToUpdate.setImages(imagesInRealty);

                System.out.println(imagesInRealty);

                System.out.println(realty_objectToUpdate);
                realtyObjectDAO.save(realty_objectToUpdate);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            for(int i=0;i<imagesToDelete.length;i++){
                String pathToFileToDlt=imagesToDelete[i];
                System.out.println(pathToFileToDlt);
                List<String> aOfStro= List.of(pathToFileToDlt.split("/", 4));
                System.out.println(aOfStro);
                String spPthToFile=aOfStro.get(aOfStro.size()-1);
                System.out.println(spPthToFile);
                String fileDirectoryName = path.concat(spPthToFile);
                try {
                    Files.delete(Path.of(fileDirectoryName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } else if (imagesToAdd!=null && imagesToDelete==null) {
            System.out.println("New images but current images doesnt need to delete");

            try {
                Realty_Object object=mapper.readValue(realtyObject,Realty_Object.class);
                System.out.println("new images");
                realty_objectToUpdate.setDistrict(object.getDistrict());
                realty_objectToUpdate.setAddress(object.getAddress());
                realty_objectToUpdate.setApt_suite_building(object.getApt_suite_building());
                realty_objectToUpdate.setRooms(object.getRooms());
                realty_objectToUpdate.setSquare(object.getSquare());
                realty_objectToUpdate.setDetails(object.getDetails());
                realty_objectToUpdate.setReal_estate(object.getReal_estate());
//                realty_objectToUpdate.setPrice(object.getPrice());
                realty_objectToUpdate.setDateOfUpdate(formater.format(object.getUpdateDate()));

                realty_objectToUpdate.getPrice().setCurrency(object.getPrice().getCurrency());
                realty_objectToUpdate.getPrice().setSum(object.getPrice().getSum());
                realty_objectToUpdate.getPrice().setType_of_order_of_real_estate(object.getPrice().getType_of_order_of_real_estate());

                String uId=userId+"id";
                System.out.println(uId);
                String home = System.getProperty("user.home");
                String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                        File.separator+"images"+File.separator+uId+File.separator;
                System.out.println(Arrays.toString(imagesToAdd));

                List<String> currentRealtyImages=realty_objectToUpdate.getImages();
                Arrays.asList(imagesToAdd).stream().forEach(multipartFile -> {
                    try {
                        multipartFile.transferTo(new File(path+multipartFile.getOriginalFilename()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    currentRealtyImages.add(multipartFile.getOriginalFilename());
                    System.out.println(currentRealtyImages);
                });

                System.out.println(realty_objectToUpdate);
                realtyObjectDAO.save(realty_objectToUpdate);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }

    }
    @PostMapping("/{id}/addObject")
    public void addObject(@PathVariable int id,@RequestParam("body") String realty_object, @RequestParam MultipartFile[] images) {

        Customer customer=customerDAO.findCustomerById(id);
        List<Realty_Object> customerList=customer.getMy_realty_objectList();
        SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");


//    System.out.println(realty_object);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Realty_Object object=mapper.readValue(realty_object,Realty_Object.class);

            Realty_Object newRealtyObject=new Realty_Object();
            newRealtyObject.setDistrict(object.getDistrict());
            newRealtyObject.setAddress(object.getAddress());
            newRealtyObject.setApt_suite_building(object.getApt_suite_building());
            newRealtyObject.setRooms(object.getRooms());
            newRealtyObject.setSquare(object.getSquare());
            newRealtyObject.setReal_estate(object.getReal_estate());
            newRealtyObject.setPrice(object.getPrice());
            newRealtyObject.setDateOfCreation(formater.format(object.getCreationDate()));
            newRealtyObject.setDetails(object.getDetails());
//        newRealtyObject.setDetailsTWO(object.getDetailsTWO());
//        newRealtyObject.setImages("/images/"+images.getOriginalFilename());

            String home = System.getProperty("user.home");

            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator+"images"+File.separator;
//        String directoryName = path.concat(customer.getName()+customer.getSurname());
            String directoryName = path.concat(customer.getId()+"id");
//        String fileName = id + getTimeStamp() + ".txt";

            File directory = new File(directoryName);
            if (! directory.exists()){
                directory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }

            List<String> images1 = newRealtyObject.getImages();
            Arrays.asList(images).stream().forEach(multipartFile -> {
                try {
                    multipartFile.transferTo(new File(home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                            File.separator+"images"+File.separator+directory.getName()+File.separator+multipartFile.getOriginalFilename()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                images1.add(multipartFile.getOriginalFilename());

            });
            System.out.println(newRealtyObject);
            customerList.add(newRealtyObject);
            realtyObjectDAO.save(newRealtyObject);

        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    @PostMapping("/getSelectedRealtyObjects")
    public ResponseEntity<List<Map<Integer,Realty_Object>>> getSelectedRealtyObjects(@RequestParam("selectType") String selected, @RequestParam("inputData") String input){
        return realtyObjectService.getSelectedRealtyObjects(selected,input);
    }
    @GetMapping("/object/{id}")
    public ResponseEntity<Realty_Object> getObject(@PathVariable int id){
        Realty_Object realty_object= realtyObjectDAO.findRealty_ObjectById(id);
        return new ResponseEntity<>(realty_object, HttpStatus.OK);
    }
    @GetMapping("/getAllRealtyObjects")
    public ResponseEntity<List<Realty_Object>> getAllRealtyObjects(){
        return new ResponseEntity<>(realtyObjectDAO.findAll(),HttpStatus.OK);
    }
    @GetMapping("/get12RandomRealtyObject")
    public ResponseEntity<List<Realty_Object>> getRealtyObjects(){
        return realtyObjectService.get12RandomRealtyObject();
    }
}
