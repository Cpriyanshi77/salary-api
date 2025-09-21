package com.opstree.microservice.salary.swagger;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI myOpenAPI() {
    Server devServer = new Server();
    devServer.setUrl("http://lb-test-1996005186.ap-south-1.elb.amazonaws.com:8081");  //add frontend ip
    devServer.setDescription("Server URL in Development environment");

    Server albServer = new Server();
    albServer.setUrl("http://lb-test-1996005186.ap-south-1.elb.amazonaws.com:8081/swagger-ui/index.html");
    albServer.setDescription("Server URL in Development environment");


    Server albbServer = new Server();
    albbServer.setUrl("http://lb-test-1996005186.ap-south-1.elb.amazonaws.com:8081/actuator/health");
    albbServer.setDescription("Server URL in Development environment");

    Contact contact = new Contact();
    contact.setEmail("opensource@opstree.com");
    contact.setName("Opstree Solutions");
    contact.setUrl("https://opstree.com");

    License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

    Info info = new Info()
        .title("Salary Microservice API")
        .version("1.0")
        .contact(contact)
        .description("This API exposes endpoints to manage salary information.").termsOfService("https://www.opstree.com/terms")
        .license(mitLicense);

    return new OpenAPI().info(info).servers(List.of(devServer, albServer, albbServer));
  }
  @Bean
public CorsFilter corsFilter() {
  CorsConfiguration config = new CorsConfiguration();
  config.addAllowedOrigin("*");
  config.addAllowedMethod("*");
  config.addAllowedHeader("*");
  UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
  source.registerCorsConfiguration("/**", config);
  return new CorsFilter(source);
}
}


