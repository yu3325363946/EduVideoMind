package it;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("it.mapper")
public class ItApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItApplication.class, args);
    }
}

