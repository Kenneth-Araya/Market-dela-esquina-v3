package cl.duoc.msVenta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SistemaMarketApplication {
	public static void main(String[] args) {
		SpringApplication.run(SistemaMarketApplication.class, args);
	}
}