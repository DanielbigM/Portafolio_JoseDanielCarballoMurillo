package TechShop.JoseDaniel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import TechShop.JoseDaniel.service.UsuarioDetailsService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    
    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {

        UserDetails juan = User.builder()
                .username("juan")
                .password(passwordEncoder.encode("123"))
                .roles("ADMIN")
                .build();

        UserDetails rebeca = User.builder()
                .username("rebeca")
                .password(passwordEncoder.encode("123"))
                .roles("VENDEDOR")
                .build();

        UserDetails pedro = User.builder()
                .username("pedro")
                .password(passwordEncoder.encode("123"))
                .roles("USUARIO")
                .build();

        return new InMemoryUserDetailsManager(
                juan,
                rebeca,
                pedro
        );
    }

     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**",
                        "/js/**", "/images/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                )
                .formLogin(login -> login
                .loginPage("/login")
                .permitAll()
                );

        return http.build();
    }

    private final UsuarioDetailsService usuarioDetailsService;

    public SecurityConfig(
            UsuarioDetailsService usuarioDetailsService) {

        this.usuarioDetailsService
                = usuarioDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider
                = new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                usuarioDetailsService
        );

        provider.setPasswordEncoder(
                passwordEncoder()
        );

        return provider;
    }
    
    
}
