package uz.pdp.online.oxoinjavawithrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class OxOinJavaWithRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OxOinJavaWithRestApiApplication.class, args);

        for (int i = 0; i < 10; i++) {
            System.out.println(new Random().nextInt(1, 3));
        }
    }

}
