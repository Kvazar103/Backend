package com.example.backend.controllers;

import com.example.backend.dao.CustomerDAO;
//import com.example.backend.dao.UserDAO;
import com.example.backend.dao.RealtyObjectDAO;
import com.example.backend.models.Customer;
//import com.example.backend.services.CustomerService;
import com.example.backend.models.Price;
import com.example.backend.models.Real_Estate;
import com.example.backend.models.Realty_Object;
import com.example.backend.models.dto.CustomerDTO;
import com.example.backend.models.dto.RealtyObjectDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
//import org.springframework.context.annotation.Bean;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;



@RestController
@AllArgsConstructor
@CrossOrigin("http://localhost:3000")
public class MainController {
    private CustomerDAO customerDAO;
    private RealtyObjectDAO realtyObjectDAO;
    private PasswordEncoder passwordEncoder;
    BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
//    boolean isPasswordMatches = bcrypt.matches(userenteredpasswordWithotEncryoted, encryptedPasswordFromDb);

    private AuthenticationManager authenticationManager;//базовий об'єкт який займається процесом аутентифікації


//    @PatchMapping("/{id}/changePassword")
//    public void changePassword(@PathVariable int id,@RequestBody String customerPassword){
//        Customer customerToChangePassword=customerDAO.findCustomerById(id);
//        System.out.println(customerPassword);
//        boolean isPasswordMatches = bcrypt.matches(customerPassword, customerToChangePassword.getPassword());
//        if(isPasswordMatches){
//            System.out.println("passwords matches");
//        }else {
//            System.out.println("wrong");
//        }
//    }
    @PatchMapping("/{id}/checkPassword")
    public ResponseEntity<String> checkIsPasswordMatches(@PathVariable int id,@RequestParam("old_password") String customerPassword,@RequestParam("new_password") String newPassword){
        Customer customerToCheckPassword=customerDAO.findCustomerById(id);
        System.out.println(customerPassword);
        System.out.println("customer password");
        System.out.println(newPassword);
        System.out.println("new password");
        boolean isPasswordMatches = bcrypt.matches(customerPassword, customerToCheckPassword.getPassword());
        if(isPasswordMatches){
            System.out.println("passwords matches");
            customerToCheckPassword.setPassword(passwordEncoder.encode(newPassword));
            customerDAO.save(customerToCheckPassword);
            return new ResponseEntity<>("Password true",HttpStatus.OK);
        }else {
            System.out.println("wrong");
            return new ResponseEntity<>("Password false",HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/save")
    public void save(@RequestParam("customer") String customer,@RequestParam MultipartFile avatar) {
        ObjectMapper mapper=new ObjectMapper();
        try {
            Customer object=mapper.readValue(customer,Customer.class);

            Customer newCustomer=new Customer();
            newCustomer.setName(object.getName());
            newCustomer.setSurname(object.getSurname());
            newCustomer.setEmail(object.getEmail());
            newCustomer.setLogin(object.getLogin());
            newCustomer.setPassword(passwordEncoder.encode(object.getPassword()));
            newCustomer.setPhone_number(object.getPhone_number());


            System.out.println(newCustomer);

            String home = System.getProperty("user.home");

            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                    File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                    File.separator+"java"+File.separator+"com"+File.separator+"example"+
                    File.separator+"backend"+File.separator+"images"+File.separator;
            String directoryName = path.concat(newCustomer.getName()+newCustomer.getSurname()+"_avatar");

            File directory = new File(directoryName);
            if (! directory.exists()){
                directory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }
            avatar.transferTo(new File(home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                    File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                    File.separator+"java"+File.separator+"com"+File.separator+"example"+
                    File.separator+"backend"+File.separator+"images"+File.separator+directory.getName()+File.separator+avatar.getOriginalFilename()));

            newCustomer.setAvatar(avatar.getOriginalFilename());
            customerDAO.save(newCustomer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//       customerDAO.save(customer);
    }
    @PatchMapping("/{id}/updateProfile")
    public void updateProfile(@PathVariable int id,@RequestParam("customer") String customer,@RequestParam(required = false) MultipartFile avatar,@RequestParam(required = false) String message) throws IOException{

        ObjectMapper mapper=new ObjectMapper();
        Customer customerToUpdate= customerDAO.findCustomerById(id);
        System.out.println(customerToUpdate);

        if(message!=null && message.equals("Deleted")){
            System.out.println("deleeee");
            String home = System.getProperty("user.home");
            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                    File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                    File.separator+"java"+File.separator+"com"+File.separator+"example"+
                    File.separator+"backend"+File.separator+"images"+File.separator;
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
                customerToUpdate.setPhone_number(object.getPhone_number());
                System.out.println("dfssdfsd");
                System.out.println(customerToUpdate);
                System.out.println(message);
//                System.out.println(avatar);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            customerToUpdate.setAvatar(null);
            customerDAO.save(customerToUpdate);
        }else if(message!=null && message.equals("NotChanged")){
            System.out.println("No change");

            String home = System.getProperty("user.home");
            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                    File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                    File.separator+"java"+File.separator+"com"+File.separator+"example"+
                    File.separator+"backend"+File.separator+"images"+File.separator;
            String directoryOldName = path.concat(customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar");

            String directoryOldNameFile = home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                    File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                    File.separator+"java"+File.separator+"com"+File.separator+"example"+
                    File.separator+"backend"+File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator;


            File oldDir=new File(home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                    File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                    File.separator+"java"+File.separator+"com"+File.separator+"example"+
                    File.separator+"backend"+File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator);

            Path oldSource=Paths.get(home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                    File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                    File.separator+"java"+File.separator+"com"+File.separator+"example"+
                    File.separator+"backend"+File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator);

            try {
                Customer object = mapper.readValue(customer, Customer.class);

                customerToUpdate.setName(object.getName());
                customerToUpdate.setSurname(object.getSurname());
                customerToUpdate.setEmail(object.getEmail());
                customerToUpdate.setLogin(object.getLogin());
                customerToUpdate.setPhone_number(object.getPhone_number());
                System.out.println("dfssdfsd");
                System.out.println(customerToUpdate);
//                System.out.println(message);
//                System.out.println(avatar);

                String newDirectory = path.concat(customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar");
                String directoryOldNewFile = home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                        File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                        File.separator+"java"+File.separator+"com"+File.separator+"example"+
                        File.separator+"backend"+File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator;

                File newDir=new File(home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                        File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                        File.separator+"java"+File.separator+"com"+File.separator+"example"+
                        File.separator+"backend"+File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator+customerToUpdate.getAvatar());

                Path newSource=Paths.get(home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                        File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                        File.separator+"java"+File.separator+"com"+File.separator+"example"+
                        File.separator+"backend"+File.separator+"images"+File.separator+customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar"+File.separator);
                Files.move(oldSource,newSource);
                customerDAO.save(customerToUpdate);

                    File fileList =oldDir.getCanonicalFile();
                    System.out.println(fileList);

                        boolean b = oldDir.renameTo(newDir);
                    System.out.println(b);

                System.out.println("abs");

            } catch (IOException ignored) {

            }
//            customerDAO.save(customerToUpdate);
        }else if(avatar == null){
            System.out.println("avatar empty");

            String home = System.getProperty("user.home");
            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                    File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                    File.separator+"java"+File.separator+"com"+File.separator+"example"+
                    File.separator+"backend"+File.separator+"images"+File.separator;
            String directoryOldName = path.concat(customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar");

            try {
                Customer object = mapper.readValue(customer, Customer.class);

                customerToUpdate.setName(object.getName());
                customerToUpdate.setSurname(object.getSurname());
                customerToUpdate.setEmail(object.getEmail());
                customerToUpdate.setLogin(object.getLogin());
                customerToUpdate.setPhone_number(object.getPhone_number());
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
        }
        if(avatar!=null){
            customerToUpdate.setAvatar(null);
            String home = System.getProperty("user.home");

            String path= home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                    File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                    File.separator+"java"+File.separator+"com"+File.separator+"example"+
                    File.separator+"backend"+File.separator+"images"+File.separator;
            String directoryOldName = path.concat(customerToUpdate.getName()+customerToUpdate.getSurname()+"_avatar");
            try {
                Customer object = mapper.readValue(customer, Customer.class);

                customerToUpdate.setName(object.getName());
                customerToUpdate.setSurname(object.getSurname());
                customerToUpdate.setEmail(object.getEmail());
                customerToUpdate.setLogin(object.getLogin());
                customerToUpdate.setPhone_number(object.getPhone_number());
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
//            File directory = new File(directoryOldName);

            if (! nDirectory.exists()){
                nDirectory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }

//            if(directory.renameTo(new File(newDirectory))){
//                System.out.println(directory);
//            }

            System.out.println(customerToUpdate);
            try {
                avatar.transferTo(new File(home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                        File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                        File.separator+"java"+File.separator+"com"+File.separator+"example"+
                        File.separator+"backend"+File.separator+"images"+File.separator+nDirectory.getName()+File.separator+avatar.getOriginalFilename()));
                customerToUpdate.setAvatar(avatar.getOriginalFilename());
                customerDAO.save(customerToUpdate);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }
    @PatchMapping("/update/customer/{id}/addedToFavoriteList")
    public void updateCustomerAddedToFavorite(@PathVariable int id,@RequestParam("realtyObject") String realty_object){
      Customer customerToUpdate =  customerDAO.findCustomerById(id);
//      System.out.println(customerToUpdate);
//      System.out.println(realty_object);

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
    @PatchMapping("/delete/customer/{id}/addedToFavoriteRealtyObject/{x}")
    public void deleteRealtyObjectAddedToFavorite(@PathVariable int id,@PathVariable int x){
        Customer customer=customerDAO.findCustomerById(id);
        System.out.println(customer);
        List<Integer> addedToFavorite=customer.getAdded_to_favorites();
//        for (Integer el:addedToFavorite){
//            if(el == x){
//                System.out.println(el);
//                addedToFavorite.remove(el);
//                customer.setAdded_to_favorites(addedToFavorite);
//                customerDAO.save(customer);
//            }
//        }
        for (int i=0;i<addedToFavorite.size();i++){
            if(addedToFavorite.get(i).equals(x)){
                addedToFavorite.remove(addedToFavorite.get(i));
                customer.setAdded_to_favorites(addedToFavorite);
                customerDAO.save(customer);
            }
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CustomerDTO customerDTO){  //метод логін для того що віддав нам токен
        if(!customerDAO.existsCustomerByLogin(customerDTO.getLogin())){
            System.out.println("Wrong login");
            return  new ResponseEntity<>("Error",HttpStatus.FORBIDDEN);
        }
        Authentication authenticate= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customerDTO.getLogin(),customerDTO.getPassword()));  //тут ми впроваджуємо об'єкт який має мати аутентифікацію(креденшили)
        // і коли ми його тут вставляєм то спрацьовує метод configure(AuthenticationManagerBuilder auth) з SecurityConfig і якщо він його там знайде то впроваде ідентифікацію(заповнить authenticate)
        if(authenticate!=null){ //якщо authenticate заповнений тоді згенеруємо токен
          Customer customer= customerDAO.findCustomerByLogin(customerDTO.getLogin());
            String jwtToken= Jwts.builder().
                    setSubject(authenticate.getName()) //тут ми передаємо ім'я і саме його ми будемо кодувати
                    .setExpiration(new Date()) //час токена
                    .signWith(SignatureAlgorithm.HS512,"nazar".getBytes(StandardCharsets.UTF_8)) //тут є саме кодування
                    .compact(); //це позволить зробити стрінгу яка й буде являтися токеном
            System.out.println(jwtToken);
            HttpHeaders headers=new HttpHeaders();
            headers.add("Authorization","Bearer "+jwtToken);//додаємо в хедер наш токен
            return new ResponseEntity<>(customer,headers,HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("zazazazaz",HttpStatus.FORBIDDEN);//якщо провірку не пройшло тоді заборонено
    }
//    @PostMapping("/{id}/addObject")
//    public ResponseEntity<?> addObject(@PathVariable int id, @RequestBody Realty_Object realty_object){
//        Customer customer=customerDAO.findCustomerById(id);
//       List<Realty_Object> list= customer.getMy_realty_objectList();
//       list.add(realty_object);
//        realtyObjectDAO.save(realty_object);
//        return new ResponseEntity<>(realty_object, HttpStatus.CREATED);
//    }


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

       String path= home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                File.separator+"java"+File.separator+"com"+File.separator+"example"+
                File.separator+"backend"+File.separator+"images"+File.separator;
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
            multipartFile.transferTo(new File(home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                File.separator+"java"+File.separator+"com"+File.separator+"example"+
                File.separator+"backend"+File.separator+"images"+File.separator+directory.getName()+File.separator+multipartFile.getOriginalFilename()));
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

    @GetMapping("/object/{id}")
    public ResponseEntity<Realty_Object> getObject(@PathVariable int id){
       Realty_Object realty_object= realtyObjectDAO.findRealty_ObjectById(id);
        return new ResponseEntity<>(realty_object,HttpStatus.OK);
    }
    @GetMapping("/customer/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable int id){
        Customer customer=customerDAO.findCustomerById(id);
        return new ResponseEntity<>(customer,HttpStatus.OK);
    }
    @DeleteMapping("/deleteAll")
    public void delete(){
        customerDAO.deleteAll();
    }
    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<Customer>> getCustomers(){
        return new ResponseEntity<>(customerDAO.findAll(), HttpStatus.OK);
    }
    @GetMapping("/getAllRealtyObjects")
    public ResponseEntity<List<Realty_Object>> getAllRealtyObjects(){
        return new ResponseEntity<>(realtyObjectDAO.findAll(),HttpStatus.OK);
    }
    @GetMapping("/get12RandomRealtyObject")
    public ResponseEntity<List<Realty_Object>> getRealtyObjects(){
        List<Realty_Object> list=realtyObjectDAO.findAll();
        Collections.shuffle(list);
        int numberOfElements=12;

        List<Realty_Object> newList=list.subList(0,numberOfElements);
        return new ResponseEntity<>(newList,HttpStatus.OK);
    }

    @PatchMapping("/customer/{id}/realtyObject/{x}")
    public ResponseEntity<Customer> deleteRealtyObject(@PathVariable int id,@PathVariable int x){
        Customer customer=customerDAO.findCustomerById(id);
        System.out.println(customer);
        List<Realty_Object> realty_objectList= customer.getMy_realty_objectList();
        System.out.println(realty_objectList);
        for (Realty_Object el:realty_objectList) {
            if(el.getId()==x){
                System.out.println(el);
                realty_objectList.remove(el);
                System.out.println(realty_objectList);
                customer.setMy_realty_objectList(realty_objectList);
                customerDAO.save(customer);
            }
        }
        return new ResponseEntity<>(customer,HttpStatus.OK);
    }




}
