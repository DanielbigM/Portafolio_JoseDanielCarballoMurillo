package TechShop.JoseDaniel;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneradorPassword {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String hash = encoder.encode("123");

        System.out.println(hash);
    }
}