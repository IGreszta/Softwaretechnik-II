package com.example.softwaretechnik2.controllers;

import com.example.softwaretechnik2.model.User;
import com.example.softwaretechnik2.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static org.hibernate.bytecode.enhance.spi.interceptor.BytecodeInterceptorLogging.LOGGER;

@AllArgsConstructor
@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        //Optional<Product> product = repo.findById(1L);
        //model.addAttribute("salamibrötchen",product.get().getProductName());
        return "login";
    }

    @GetMapping("start")
    public String start() {
        return "start";
    }


    @GetMapping("register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") User user,
                               BindingResult result, HttpServletRequest request,
                               Model model) {
        String email = user.getEmail();
        String password = user.getPassword();
        String confirmedPassword = user.getConfirmedPassword();
        User existing = userService.findByEmail(email);
        if (existing != null) {
            result.rejectValue("email", null, "bereits registriert!");
        }
        if (!password.equals(confirmedPassword)) {
            result.rejectValue("password", null, "Passwörter stimmen nicht überein");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        try {
            request.login(email, password);
        } catch (ServletException e) {
            LOGGER.error("Error while login ", e);
        }
        return "redirect:/start";
    }
}