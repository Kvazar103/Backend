package com.example.backend.controllers;

import com.example.backend.dao.CustomerDAO;
//import com.example.backend.dao.UserDAO;
import com.example.backend.models.Customer;
//import com.example.backend.services.CustomerService;
import com.example.backend.models.dto.CustomerDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
//import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


@RestController
@AllArgsConstructor
@CrossOrigin("http://localhost:3000")
public class MainController {
    private CustomerDAO customerDAO;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;//базовий об'єкт який займається процесом аутентифікації


    @PostMapping("/addUser")
    Customer newCustomer(@RequestBody Customer newCustomer){
        return customerDAO.save(newCustomer);
    }

    @GetMapping("/")
    public String open(){
        return "open";
    }

    @PostMapping("/save")
    public void save(@RequestBody Customer customer){
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
       customerDAO.save(customer);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CustomerDTO customerDTO){  //метод логін для того що віддав нам токен
        if(!customerDAO.existsCustomerByLogin(customerDTO.getLogin())){
            System.out.println("Wrong login");
            return  new ResponseEntity<>("Error",HttpStatus.FORBIDDEN);
        }
        Authentication authenticate= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customerDTO.getLogin(),customerDTO.getPassword()));  //тут ми впроваджуємо об'єкт який має мати аутентифікацію(креденшили)
        // і коли ми його тут вставляєм то спрацьовує метод configure(AuthenticationManagerBuilder auth) з SecurityConfig і якщо він його там знайде то впроваде ідентифікацію(заповнить authenticate)
        if(authenticate!=null){ //якщо authenticate заповнений тоді згенеруємо токен

            String jwtToken= Jwts.builder().
                    setSubject(authenticate.getName()) //тут ми передаємо ім'я і саме його ми будемо кодувати
                    .setExpiration(new Date()) //час токена
                    .signWith(SignatureAlgorithm.HS512,"nazar".getBytes(StandardCharsets.UTF_8)) //тут є саме кодування
                    .compact(); //це позволить зробити стрінгу яка й буде являтися токеном
            System.out.println(jwtToken);
            HttpHeaders headers=new HttpHeaders();
            headers.add("Authorization","Bearer "+jwtToken);//додаємо в хедер наш токен
            return new ResponseEntity<>("you are log in",headers, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("zazazazaz",HttpStatus.FORBIDDEN);//якщо провірку не пройшло тоді заборонено
    }
    @DeleteMapping("/deleteAll")
    public void delete(){
        customerDAO.deleteAll();
    }
    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<Customer>> getCustomers(){
        return new ResponseEntity<>(customerDAO.findAll(), HttpStatus.OK);
    }

}
