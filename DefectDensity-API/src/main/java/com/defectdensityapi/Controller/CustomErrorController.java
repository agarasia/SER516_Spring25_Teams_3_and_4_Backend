package com.defectdensityapi.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
public class CustomErrorController implements ErrorController {



    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        return "Oops! The page you're looking for doesn't exist. Use /repo?url={desired github url in the format of https://github.com/[Owner]/[project name]}";
    }

    public String getErrorPath() {
        return "/error";
    }
}