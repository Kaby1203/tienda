package com.tienda;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public static final String[] PUBLIC_URL = {"/", "/index", "/fav/**", "/js/**", "/webjars/**", "/login", "/acceso_denagado"};
    public static final String[] USUARIO_URL = {"/facturar/carrito"};
    public static final String[] ADMIN_OR_VENDEDOR_URL = {"/producto/listado", "/categoria/listado", "/usuario/listado"};
    public static final String[] ADMIN_URL = {"/producto/**", "/categoria/**", "/usuario/**", "/consultas/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_URL).permitAll()
                .requestMatchers(USUARIO_URL).hasRole("USUARIO")
                .requestMatchers(ADMIN_OR_VENDEDOR_URL).hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(ADMIN_URL).hasRole("ADMIN")
                .anyRequest().authenticated()
        ).formLogin(form -> form // Configuración de formulario de login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
        ).logout(logout -> logout // Configuración de logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        ).exceptionHandling(exceptions -> exceptions // Manejo de excepciones
                .accessDeniedPage("/acceso_denegado")
        ).sessionManagement(session -> session // Configuración de sesiones
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Este método será reemplazado la siguiente semana
    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails juan = User.builder()
                .username("juan")
                .password(passwordEncoder.encode("123"))
                .roles("ADMIN")
                .build();
        UserDetails rebeca = User.builder()
                .username("rebeca")
                .password(passwordEncoder.encode("456"))
                .roles("VENDEDOR")
                .build();
        UserDetails pedro = User.builder()
                .username("pedro")
                .password(passwordEncoder.encode("789"))
                .roles("USUARIO") // Consistent con tu configuración
                .build();
        return new InMemoryUserDetailsManager(juan, rebeca, pedro);
    }
}
