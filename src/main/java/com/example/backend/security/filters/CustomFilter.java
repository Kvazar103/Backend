package com.example.backend.security.filters;

import com.example.backend.dao.CustomerDAO;
import com.example.backend.models.Customer;
import com.example.backend.models.dto.CustomerLoginPasswordRoleDTO;
import io.jsonwebtoken.Jwts;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;


public class CustomFilter extends OncePerRequestFilter {//OncePerRequestFilter в нього вбудований механізм який відпрацює лише один раз за ріквест

    private CustomerDAO customerDAO;

    public CustomFilter(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        //тут ми відхоплюємо запити які прийдуть зі сторони користувача з токеном

        String authorization =request.getHeader("Authorization");//тут ми кажемо який хедер хочемо відхопити

        if(authorization!=null && authorization.startsWith("Bearer ")){//якщо authorization не пустий і починається з префіксу Bearer тоді це насправді токен
            System.out.println(authorization);
            System.out.println("filter trig");

            String token =authorization.replace("Bearer ","");//ми зможемо "відкусити" токен
            String subject= Jwts.parser() //розшифровуєм токен
                    .setSigningKey("nazar".getBytes(StandardCharsets.UTF_8)) //без наявності секретного ключа нічого не вийде
                    .parseClaimsJws(token)//після розшифровки ми витягуємо інформацію з нього
                    .getBody() //вся інформація знаходиться тут
                    .getSubject(); //з body ми витягуємо лише саму необхідну інформацію
            System.out.println(subject);//asd

//            Customer customerByLogin=customerDAO.findCustomerByLogin(subject);
            Customer customerByLogin=customerDAO.findCustomerById(Integer.valueOf(subject));



//            CustomerLoginPasswordRoleDTO customerLoginPasswordRoleDTO=new CustomerLoginPasswordRoleDTO(customerByLogin.getLogin(),customerByLogin.getPassword(),customerByLogin.getRole());
            System.out.println("customer by id");
            System.out.println(customerByLogin);
            System.out.println("customer by id");

            if(customerByLogin!=null){ //якщо ми найшли customer(бо якщо нічого не знайде в бд то воно поверне null)
                SecurityContextHolder.getContext().setAuthentication(//аутентифікація
                        new UsernamePasswordAuthenticationToken(
//                                customerByLogin.getLogin(),
                                customerByLogin.getId(),
                                customerByLogin.getPassword(),
                                Collections.singletonList(new SimpleGrantedAuthority(customerByLogin.getRole()))
                        )
                );

            }
        }
        filterChain.doFilter(request,response);//без того не буде працювати
    }
}
