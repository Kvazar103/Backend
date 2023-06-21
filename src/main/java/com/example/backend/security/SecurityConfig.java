package com.example.backend.security;

import com.example.backend.dao.CustomerDAO;
import com.example.backend.models.Customer;
import com.example.backend.security.filters.CustomFilter;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.transaction.Transactional;
import java.util.Arrays;


@Configuration  //анотація щоб створювати @BEAN
@EnableWebSecurity  //впроваджує дефолтні налаштування щоб наше секюріті почала обробку запитів
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private CustomerDAO customerDAO;

    @Bean // те що повертається з метода робиться об'єктом і кладе його під контейнер(який можна використовувати в MainController
    PasswordEncoder passwordEncoder(){ //розшифровує пароль
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CustomFilter customFilter(){
        return new CustomFilter(customerDAO);
    }
    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {//позволяє поставити цей обєкт в bean контейнер
        // і після цього з bean контейнера його можна викликати в maincontroller
        return super.authenticationManager();//тут ми беремо менеджер аутентифікації який присутній в WebSecurityConfigurerAdapter і робимо з нього bean
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // тут ми приймаємо логін пароль і знайти обєкт в базі даних
        auth.userDetailsService(username -> { //знайти обєкт в бд
            System.out.println("login trig");
            System.out.println(username);

            Customer customer=customerDAO.findCustomerByLogin(username);
            if(!customerDAO.existsCustomerByLogin(username)){
                System.out.println("Wrong login");

            }
            return new User(customer.getLogin(),
                    customer.getPassword(),
                    Arrays.asList(new SimpleGrantedAuthority(customer.getRole()))
                    );
        });
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        //тут ми пишемо з яких додаткових хостів можна звертатися до нашої програми(які методи дозволені,хедери і.т.д)
        CorsConfiguration configuration=new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));//з тих хостів ми дозволяємо звертатися до нашої програми
        configuration.addAllowedHeader("*");//header- додаткова метаінформація(логін пароль і.т.д)метод каже що всі хедери позволені
        configuration.setAllowedMethods(
                Arrays.asList( //тут ми пишемо які методи дозволені в хості (також можна забороняти будь-які методи з хоста)
                        HttpMethod.GET.name(), //.name() - для того щоб перетворити назву http метода на стрінгу
                        HttpMethod.PUT.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.HEAD.name()
                ));
        configuration.addExposedHeader("Authorization");//щоб бачила наші хедери які являються нашими кастомними
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();//привязуємо конфігурації до певної урли
        source.registerCorsConfiguration("/**",configuration);//будь-які урли які будуть тут появлятися ми цю конфігурацію застосовуємо
        return source;
        //після того як бін готовий викликаємо його в configure в .and().cors().configurationSource(corsConfigurationSource())
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
         http.csrf().disable()
                 .authorizeRequests()
                 .antMatchers(HttpMethod.GET,"/").permitAll()
                 .antMatchers(HttpMethod.POST,"/save").permitAll()
                 .antMatchers(HttpMethod.DELETE,"/deleteAll").authenticated()
                 .antMatchers(HttpMethod.GET,"/getAllCustomers").permitAll()
                 .antMatchers(HttpMethod.POST,"/login").permitAll()
                 .antMatchers(HttpMethod.GET,"/get12RandomRealtyObject").permitAll()
                 .antMatchers(HttpMethod.PATCH,"/customer/{id}/realtyObject/{x}").permitAll()
                 .antMatchers(HttpMethod.PATCH,"/{id}/updateProfile").authenticated()
                 .antMatchers(HttpMethod.POST,"/{id}/addObject").authenticated()
                 .antMatchers(HttpMethod.PATCH,"/{id}/checkPassword").authenticated()
                 .antMatchers(HttpMethod.PATCH,"/update/customer/{id}/addedToFavoriteList").authenticated()
                 .antMatchers(HttpMethod.DELETE,"/delete/customer/{id}/addedToFavoriteRealtyObject/{x}").authenticated()
                 .antMatchers(HttpMethod.DELETE,"/customer/{id}/realtyObject/{x}").authenticated()
                 .antMatchers(HttpMethod.PATCH,"/{id}/{userId}/updateRealtyObject").authenticated()
                 .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//щоб не зберігалася сесія
        // бо якщо буде зберігатися сесія сервак буде кешувати токен(він може мати закешований і все одно пустить якщо буде заборонено)
                 .and().cors().configurationSource(corsConfigurationSource()) //за замовчуванням дозволено зробити запит до ендпоїнтів тільки з одного сервака(запит з localhost:8080 тільки на localhost:8080)/тому ми додали додаткові
                 .and().addFilterBefore(customFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
