package br.dev.marcosilva.springkafka;

import br.dev.marcosilva.springkafka.properties.KafkaProperties;
import br.dev.marcosilva.springkafka.properties.StorageProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {
		"br.dev.marcosilva.springkafka",
		"br.dev.marcosilva.fileaxe.axe",
		"br.dev.marcosilva.fileaxe.storage"})
@EnableConfigurationProperties({StorageProperties.class, KafkaProperties.class})
public class FileAxeKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileAxeKafkaApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
		return new OpenAPI()
				.components(new Components())
				.info(new Info().title("Spring Kafka").version(appVersion)
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}
}
