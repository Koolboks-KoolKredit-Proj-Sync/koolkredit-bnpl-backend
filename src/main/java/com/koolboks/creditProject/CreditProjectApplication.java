package com.koolboks.creditProject;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "Kool Kredit API",
        version = "1.0",
        description = "KoolKredit API documentation"
    )
)






@EnableAsync
@EnableScheduling
@SpringBootApplication
public class CreditProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditProjectApplication.class, args);
	}

//	@Bean
//	public RestTemplate restTemplate() {
//		return new RestTemplate();
//	}

}
