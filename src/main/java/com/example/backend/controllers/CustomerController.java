package com.example.backend.controllers;


import com.example.backend.dao.CustomerDAO;
import com.example.backend.models.Customer;
import com.example.backend.models.Realty_Object;
import com.example.backend.models.dto.CustomerDTO;
import com.example.backend.models.dto.CustomerNoPasswordDTO;
import com.example.backend.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.hibernate.Hibernate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@AllArgsConstructor
@CrossOrigin("http://localhost:3000")
public class CustomerController {
    private CustomerDAO customerDAO;
    private CustomerService customerService;
    private PasswordEncoder passwordEncoder;
    BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
    private AuthenticationManager authenticationManager;//базовий об'єкт який займається процесом аутентифікації

    @PatchMapping("/{id}/checkPassword")
    public ResponseEntity<String> checkIsPasswordMatches(@PathVariable int id, @RequestParam("old_password") String customerPassword, @RequestParam("new_password") String newPassword){
        return customerService.ifPasswordMatchesSave(id,customerPassword,newPassword);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestParam("customer") String customer,@RequestParam MultipartFile avatar) {
        ObjectMapper mapper=new ObjectMapper();
        try {
            Customer object=mapper.readValue(customer,Customer.class);

            Customer newCustomer=new Customer();
            newCustomer.setName(object.getName());
            newCustomer.setSurname(object.getSurname());
            newCustomer.setEmail(object.getEmail());
            newCustomer.setLogin(object.getLogin());
            newCustomer.setPassword(object.getPassword());
            newCustomer.setPhone_number(object.getPhone_number());

            List<String> responses= customerService.validateCustomer(newCustomer);
            System.out.println("response");
            System.out.println(responses);
            if(responses.size()>0 && responses.get(0).equals("noErrors")){
                System.out.println("noError");
            }else if(responses.size()>0){
                System.out.println("Errors");
                System.out.println(responses);
                return new ResponseEntity<>(responses,HttpStatus.BAD_REQUEST);
            }

            System.out.println(newCustomer);
            newCustomer.setPassword(passwordEncoder.encode(object.getPassword()));


            String home = System.getProperty("user.home");

            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator+"images"+File.separator;
            String directoryName = path.concat(newCustomer.getName()+newCustomer.getSurname()+"_avatar");

            File directory = new File(directoryName);
            if (! directory.exists()){
                directory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }
            avatar.transferTo(new File(home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator+"images"+File.separator+directory.getName()+File.separator+avatar.getOriginalFilename()));

            newCustomer.setAvatar(avatar.getOriginalFilename());
            customerDAO.save(newCustomer);
            return new ResponseEntity<>(newCustomer,HttpStatus.CREATED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//       customerDAO.save(customer);
    }
    @PatchMapping("/{id}/updateProfile")
    public ResponseEntity<?> updateProfile(@PathVariable int id,@RequestParam("customer") String customer,@RequestParam(required = false) MultipartFile avatar,@RequestParam(required = false) String message) throws IOException{

        ObjectMapper mapper=new ObjectMapper();
        Customer customerToUpdate= customerDAO.findCustomerById(id);
        System.out.println(customerToUpdate);


        if(message!=null && message.equals("Deleted")){
            String home = System.getProperty("user.home");
            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator+"images"+File.separator;
            String directoryOldName = path.concat(customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar");
            try {
                FileUtils.deleteDirectory(new File(directoryOldName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                Customer object = mapper.readValue(customer, Customer.class);

                customerToUpdate.setName(object.getName());
                customerToUpdate.setSurname(object.getSurname());
                customerToUpdate.setEmail(object.getEmail());
                customerToUpdate.setLogin(object.getLogin());
                Customer existingCustomer=customerDAO.findCustomerByLogin(object.getLogin());
                if(existingCustomer!=null && existingCustomer.getLogin()!=object.getLogin()){
                    return new ResponseEntity<>("Login already exists",HttpStatus.FORBIDDEN);
                }
                customerToUpdate.setPhone_number(object.getPhone_number());

                List<String> responses= customerService.validateCustomer(customerToUpdate);
                System.out.println("response");
                System.out.println(responses);
                if(responses.size()>0 && responses.get(0).equals("noErrors")){
                    System.out.println("noError");
                }else if(responses.size()>0){
                    System.out.println("Errors");
                    System.out.println(responses);
                    return new ResponseEntity<>(responses,HttpStatus.BAD_REQUEST);
                }
                System.out.println(customerToUpdate);
                System.out.println(message);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            customerToUpdate.setAvatar(null);
            customerDAO.save(customerToUpdate);
            return new ResponseEntity<>(customerToUpdate,HttpStatus.OK);
        }else if(message!=null && message.equals("NotChanged")){
            System.out.println("No change");

            String home = System.getProperty("user.home");
            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator+"images"+File.separator;

            File oldDir=new File(home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator);

            Path oldSource= Paths.get(home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator);

            try {
                Customer object = mapper.readValue(customer, Customer.class);
                System.out.println("VSSS");
                customerToUpdate.setName(object.getName());
                customerToUpdate.setSurname(object.getSurname());
                customerToUpdate.setEmail(object.getEmail());
                Customer existingCustomer=customerDAO.findCustomerByLogin(object.getLogin());
                Boolean isCustomerExists=customerDAO.existsCustomerByLogin(object.getLogin());
                System.out.println(isCustomerExists);
                if(existingCustomer!=null){
                    System.out.println(existingCustomer.getName()+" "+existingCustomer.getLogin());
                    System.out.println(object.getId());
                }
                System.out.println("PPPPP");
                if(!isCustomerExists){
                    customerToUpdate.setLogin(object.getLogin());
                    System.out.println("!isCustomerExists");
                }else if(existingCustomer.getId() == customerToUpdate.getId()){
//                    customerToUpdate.setLogin(object.getLogin());
                    System.out.println("isCustomerExists && existingCustomer.getId()==object.getId()");
                }else if(existingCustomer.getId() != customerToUpdate.getId()){
                    System.out.println(" msg with Login already used");
                    return new ResponseEntity<>("Login already used",HttpStatus.BAD_REQUEST);
                }


                customerToUpdate.setPhone_number(object.getPhone_number());

                List<String> responses= customerService.validateCustomer(customerToUpdate);
                System.out.println("response");
                System.out.println(responses);
                if(responses.size()>0 && responses.get(0).equals("noErrors")){
                    System.out.println("noError");
                }else if(responses.size()>0){
                    System.out.println("Errors");
                    System.out.println(responses);
                    return new ResponseEntity<>(responses,HttpStatus.BAD_REQUEST);
                }
                System.out.println("dfssdfsd");
                System.out.println(customerToUpdate);
//                System.out.println(message);
//                System.out.println(avatar);

                File newDir=new File(home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                        File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator+customerToUpdate.getAvatar());

                Path newSource=Paths.get(home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                        File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator);
                Files.move(oldSource,newSource);
//                customerDAO.save(customerToUpdate);

                File fileList =oldDir.getCanonicalFile();
                System.out.println(fileList);

                boolean b = oldDir.renameTo(newDir);
                System.out.println(b);

                System.out.println("abs");
//                customerDAO.save(customerToUpdate);

            } catch (IOException ignored) {

            }
            customerDAO.save(customerToUpdate);
            System.out.println("abs");
            System.out.println(customerToUpdate.getId());
            System.out.println(customerToUpdate.getId());
//           return customerService.getCustomerAfterLoginUpdate(customerToUpdate.getId());

            return new ResponseEntity<>("Updated without avatar",HttpStatus.OK);

        }else if(avatar == null){
            System.out.println("avatar empty");

            String home = System.getProperty("user.home");
            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator+"images"+File.separator;
            String directoryOldName = path.concat(customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar");

            try {
                Customer object = mapper.readValue(customer, Customer.class);

                customerToUpdate.setName(object.getName());
                customerToUpdate.setSurname(object.getSurname());
                customerToUpdate.setEmail(object.getEmail());
                customerToUpdate.setLogin(object.getLogin());

                Customer existingCustomer=customerDAO.findCustomerByLogin(object.getLogin());
                Boolean isCustomerExists=customerDAO.existsCustomerByLogin(object.getLogin());
                System.out.println(isCustomerExists);
                if(existingCustomer!=null){
                    System.out.println(existingCustomer.getName()+" "+existingCustomer.getLogin());
                    System.out.println(object.getId());
                }
                System.out.println("PPPPP");
                if(!isCustomerExists){
                    customerToUpdate.setLogin(object.getLogin());
                    System.out.println("!isCustomerExists");
                }else if(existingCustomer.getId() == customerToUpdate.getId()){
//                    customerToUpdate.setLogin(object.getLogin());
                    System.out.println("isCustomerExists && existingCustomer.getId()==object.getId()");
                }else if(existingCustomer.getId() != customerToUpdate.getId()){
                    System.out.println(" msg with Login already used");
                    return new ResponseEntity<>("Login already used",HttpStatus.BAD_REQUEST);
                }
                customerToUpdate.setPhone_number(object.getPhone_number());

                List<String> responses= customerService.validateCustomer(customerToUpdate);
                System.out.println("response");
                System.out.println(responses);
                if(responses.size()>0 && responses.get(0).equals("noErrors")){
                    System.out.println("noError");
                }else if(responses.size()>0){
                    System.out.println("Errors");
                    System.out.println(responses);
                    return new ResponseEntity<>(responses,HttpStatus.BAD_REQUEST);
                }
                System.out.println("dfssdfsd");
                System.out.println(customerToUpdate);
                System.out.println(message);
//                System.out.println(avatar);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String newDirectory = path.concat(customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar");
            System.out.println(directoryOldName);
            System.out.println(newDirectory);

            try {
                FileUtils.deleteDirectory(new File(directoryOldName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File nDirectory=new File(newDirectory);
//            File directory = new File(directoryOldName);

            if (! nDirectory.exists()){
                nDirectory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }
            return new ResponseEntity<>("Avatar empty",HttpStatus.OK);
        }
        if(avatar!=null){
            customerToUpdate.setAvatar(null);
            String home = System.getProperty("user.home");

            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                    File.separator+"images"+File.separator;
            String directoryOldName = path.concat(customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar");
            try {
                Customer object = mapper.readValue(customer, Customer.class);

                customerToUpdate.setName(object.getName());
                customerToUpdate.setSurname(object.getSurname());
                customerToUpdate.setEmail(object.getEmail());
                customerToUpdate.setLogin(object.getLogin());
                Customer existingCustomer=customerDAO.findCustomerByLogin(object.getLogin());
                Boolean isCustomerExists=customerDAO.existsCustomerByLogin(object.getLogin());
                System.out.println(isCustomerExists);
                if(existingCustomer!=null){
                    System.out.println(existingCustomer.getName()+" "+existingCustomer.getLogin());
                    System.out.println(object.getId());
                }
                System.out.println("PPPPP");
                if(!isCustomerExists){
                    customerToUpdate.setLogin(object.getLogin());
                    System.out.println("!isCustomerExists");
                }else if(existingCustomer.getId() == customerToUpdate.getId()){
//                    customerToUpdate.setLogin(object.getLogin());
                    System.out.println("isCustomerExists && existingCustomer.getId()==object.getId()");
                }else if(existingCustomer.getId() != customerToUpdate.getId()){
                    System.out.println(" msg with Login already used");
                    return new ResponseEntity<>("Login already used",HttpStatus.BAD_REQUEST);
                }
                customerToUpdate.setPhone_number(object.getPhone_number());

                List<String> responses= customerService.validateCustomer(customerToUpdate);
                System.out.println("response");
                System.out.println(responses);
                if(responses.size()>0 && responses.get(0).equals("noErrors")){
                    System.out.println("noError");
                }else if(responses.size()>0){
                    System.out.println("Errors");
                    System.out.println(responses);
                    return new ResponseEntity<>(responses,HttpStatus.BAD_REQUEST);
                }
                System.out.println("dfssdfsd");
                System.out.println(customerToUpdate);
                System.out.println(message);
                System.out.println(avatar);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String newDirectory = path.concat(customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar");
            System.out.println(directoryOldName);
            System.out.println(newDirectory);

            try {
                FileUtils.deleteDirectory(new File(directoryOldName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File nDirectory=new File(newDirectory);
            if (! nDirectory.exists()){
                nDirectory.mkdir();
            }

            System.out.println(customerToUpdate);
            try {
                avatar.transferTo(new File(home+ File.separator+"Desktop"+File.separator+"real_estate_images_from_users"+
                        File.separator+"images"+File.separator+nDirectory.getName()+File.separator+avatar.getOriginalFilename()));
                customerToUpdate.setAvatar(avatar.getOriginalFilename());
                customerDAO.save(customerToUpdate);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new ResponseEntity<>("Avatar not null",HttpStatus.OK);

        }
        return new ResponseEntity<>("...",HttpStatus.OK);
    }
    @PatchMapping("/update/customer/{id}/addedToFavoriteList")
    public void updateCustomerAddedToFavorite(@PathVariable int id,@RequestParam("realtyObject") String realty_object){
        customerService.addRealtyObjectToCustomerAddedToFavoriteList(id,realty_object);
    }
    @DeleteMapping("/delete/customer/{id}/addedToFavoriteRealtyObject/{x}")
    public void deleteRealtyObjectAddedToFavorite(@PathVariable int id,@PathVariable int x){
        customerService.deleteRealtyObjectFromCustomerAddedToFavoriteList(id,x);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CustomerDTO customerDTO){  //метод логін для того що віддав нам токен
        System.out.println("Login post");
        System.out.println(customerDTO.getLogin());
        System.out.println(customerDTO.getPassword());
        Customer customerByLogin=customerDAO.findCustomerByLogin(customerDTO.getLogin());
        if(!customerDAO.existsCustomerByLogin(customerDTO.getLogin())){
            System.out.println("Wrong login");
            return  new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
        }
//        Authentication authenticate= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customerDTO.getLogin(),customerDTO.getPassword()));  //тут ми впроваджуємо об'єкт який має мати аутентифікацію(креденшили)
        Authentication authenticate= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customerByLogin.getId(),customerDTO.getPassword()));  //тут ми впроваджуємо об'єкт який має мати аутентифікацію(креденшили)
        // і коли ми його тут вставляєм то спрацьовує метод configure(AuthenticationManagerBuilder auth) з SecurityConfig і якщо він його там знайде то впроваде ідентифікацію(заповнить authenticate)
        if(authenticate!=null){ //якщо authenticate заповнений тоді згенеруємо токен
            Customer customer= customerDAO.findCustomerByLogin(customerDTO.getLogin());
            CustomerNoPasswordDTO customerWithoutPassword=new CustomerNoPasswordDTO(customer.getId(),customer.getName(),customer.getSurname(),customer.getEmail(),customer.getLogin(),customer.getPhone_number(),customer.getAvatar(),customer.getMy_realty_objectList(),customer.getAdded_to_favorites());

//            String jwtToken= Jwts.builder().
//                    setSubject(authenticate.getName()) //тут ми передаємо ім'я і саме його ми будемо кодувати
////                    .setExpiration(new Date()) //час токена
////                    .setExpiration(new Date(System.currentTimeMillis() + 864_000_000)) // 10 days
//                    .signWith(SignatureAlgorithm.HS512,"nazar".getBytes(StandardCharsets.UTF_8)) //тут є саме кодування
//                    .compact(); //це позволить зробити стрінгу яка й буде являтися токеном
//            System.out.println(jwtToken);
            String jwtToken2= Jwts.builder().
                    setSubject(Integer.toString(customer.getId()))
//                    .setExpiration(new Date()) //час токена
//                    .setExpiration(new Date(System.currentTimeMillis() + 864_000_000)) // 10 days
                    .signWith(SignatureAlgorithm.HS512,"nazar".getBytes(StandardCharsets.UTF_8)) //тут є саме кодування
                    .compact(); //це позволить зробити стрінгу яка й буде являтися токеном
            System.out.println(jwtToken2);
            HttpHeaders headers=new HttpHeaders();
//            headers.add("Authorization","Bearer "+jwtToken);//додаємо в хедер наш токен
            headers.add("Authorization","Bearer "+jwtToken2);//додаємо в хедер наш токен
            return new ResponseEntity<>(customerWithoutPassword,headers,HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("zazazazaz",HttpStatus.FORBIDDEN);//якщо провірку не пройшло тоді заборонено
    }
    @GetMapping("/customer/favorites/{id}")
    public ResponseEntity<List<Map<Integer, Realty_Object>>> getAddedToFavoriteObjects(@PathVariable int id){
        return customerService.getCurrentUserListOfAddedToFavorite(id);
    }
    @GetMapping("/customer/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable int id){
        return customerService.getCustomerById(id);
    }
    @GetMapping("/customerAfterLoginUpdate/{id}")
    public ResponseEntity<?> getCustomerAfterLoginUpdate(@PathVariable int id){
        return customerService.getCustomerAfterLoginUpdate(id);
    }
    @GetMapping("/customerLoginAndPasswordAfterLoginUpdate/{id}")
    public ResponseEntity<?> getCustomerLoginAndPasswordAfterLoginUpdate(@PathVariable int id){
        return customerService.getCustomerLoginAndPasswordAfterUpdateLogin(id);
    }

//    @GetMapping("/updated/customerWithPassword/{id}")
//    public ResponseEntity<Customer> getCustomerAfterUpdateWithPassword(@PathVariable int id){
//
//        Customer customer =customerService.getCustomerAfterUpdateWithPassword(id);
//        Authentication authenticate= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customer.getLogin(),customer.getPassword()));  //тут ми впроваджуємо об'єкт який має мати аутентифікацію(креденшили)
//
//        String jwtToken= Jwts.builder().
//                setSubject(authenticate.getName()) //тут ми передаємо ім'я і саме його ми будемо кодувати
////                    .setExpiration(new Date()) //час токена
////                    .setExpiration(new Date(System.currentTimeMillis() + 864_000_000)) // 10 days
//                .signWith(SignatureAlgorithm.HS512,"nazar".getBytes(StandardCharsets.UTF_8)) //тут є саме кодування
//                .compact(); //це позволить зробити стрінгу яка й буде являтися токеном
//        System.out.println(jwtToken);
//        HttpHeaders headers=new HttpHeaders();
//        headers.add("Authorization","Bearer "+jwtToken);//додаємо в хедер наш токен
//        return new ResponseEntity<>(customer,headers,HttpStatus.ACCEPTED);
////        return customerService.getCustomerAfterUpdateWithPassword(id);
//    }
    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<CustomerNoPasswordDTO>> getCustomers(){
        return customerService.getCustomersWithoutPassword();
    }
    @DeleteMapping("/customer/{id}/realtyObject/{x}")
    public ResponseEntity<Customer> deleteRealtyObject(@PathVariable int id,@PathVariable int x){
        return customerService.deleteRealtyObject(id,x);
    }
    @DeleteMapping("/customer/deleteProfile/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable int id){
        return customerService.deleteCustomer(id);
    }
}
