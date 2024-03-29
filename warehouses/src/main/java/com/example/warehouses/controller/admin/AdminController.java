package com.example.warehouses.controller.admin;

import com.example.warehouses.interfaces.Administrator;
import com.example.warehouses.interfaces.AdministratorFunctions;
import com.example.warehouses.model.user.UserImpl;
import com.example.warehouses.services.AdminService;
import com.example.warehouses.services.UsersService;
import com.google.common.io.ByteStreams;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@Validated
@RequestMapping(path = "hidden/admin/")
public class AdminController implements AdministratorFunctions {
    private final AdminService adminService;
    private final UsersService globalService;


    @Autowired
    public AdminController(AdminService adminService,
                           UsersService globalService) {
        this.adminService = adminService;
        this.globalService = globalService;
    }

    @SneakyThrows
    @PostMapping
    @Override
    public Optional<Administrator> isLoginAdmin(@RequestParam String email,
                                                @RequestParam String password,
                                                HttpServletResponse response) {

        Optional<Administrator> adminOpt = null;
        adminOpt = adminService.isLoginAdmin(email, password, response);
        if (adminOpt.isPresent())
        response.sendRedirect("http://localhost:8080/");

        return adminOpt;
    }

    @SneakyThrows
    @PostMapping("createClient")
    @Override
    public UserImpl createClient(@RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String clientType,
                                 HttpServletResponse response) {

        UserImpl userImplOpt = adminService.createClient(email, password, firstName, lastName, clientType, response);
        response.sendRedirect("http://localhost:8080/MainPage.html");

        return userImplOpt;
    }

    @PostMapping("createUser")
    public String createUser(@RequestParam Long adminId,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String type
    ) {
        String attempt = "User is taken!";
        if(!globalService.isUsernameTaken(email)){
            attempt = adminService.createUser(adminId,
                    email,
                    password,
                    firstName,
                    lastName,
                    type);
        }

        return attempt;
    }
    @SneakyThrows
    @GetMapping("test1")
    public void test1() {

        ClientHttpRequestFactory factory =   ClientHttpRequestFactories.get(new ClientHttpRequestFactorySettings(
                Duration.ofSeconds(1L),
                Duration.ofSeconds(1L),
                true));

       ClientHttpRequest request =  factory.createRequest(new URI("http://localhost:8080/api/main/agents"), HttpMethod.GET);
       ClientHttpResponse response = request.execute();

       request.getBody().write("{}".getBytes(UTF_8));
       InputStream stream = response.getBody();
       byte[] input = FileCopyUtils.copyToByteArray(response.getBody());
       final String message = new String(ByteStreams.toByteArray(response.getBody()));
       final String message1 = new String(FileCopyUtils.copyToByteArray(response.getBody()));

       System.out.println(message);
       System.out.println(message1);
       System.out.println(new String(input, UTF_8));


    }




}
