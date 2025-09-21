package com.opstree.microservice.salary.swagger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;

class OpenAPIConfigTests {

    @Test
    void testOpenAPIConfiguration() {
        OpenAPI openAPI = new OpenAPIConfig().myOpenAPI();

        assertNotNull(openAPI);

        // Expect 3 servers (your ELB setup)
        assertEquals(3, openAPI.getServers().size());

        Server devServer = openAPI.getServers().get(0);
        assertEquals("http://lb-test-1996005186.ap-south-1.elb.amazonaws.com:8081", devServer.getUrl());
        assertEquals("Server URL in Development environment", devServer.getDescription());

        Server albServer = openAPI.getServers().get(1);
        assertEquals("http://lb-test-1996005186.ap-south-1.elb.amazonaws.com:8081/swagger-ui/index.html", albServer.getUrl());
        assertEquals("Server URL in Development environment", albServer.getDescription());

        Server albbServer = openAPI.getServers().get(2);
        assertEquals("http://lb-test-1996005186.ap-south-1.elb.amazonaws.com:8081/actuator/health", albbServer.getUrl());
        assertEquals("Server URL in Development environment", albbServer.getDescription());

        // Basic info assertions
        assertEquals("Salary Microservice API", openAPI.getInfo().getTitle());
        assertEquals("1.0", openAPI.getInfo().getVersion());
        assertEquals("This API exposes endpoints to manage salary information.", openAPI.getInfo().getDescription());
        assertEquals("https://www.opstree.com/terms", openAPI.getInfo().getTermsOfService());
    }
}

