package com.art.galary.controllers;

import javax.naming.Binding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.art.galary.models.User;
import com.art.galary.services.UserService;

@Controller
public class RegisterationController {
    private UserService userService;

    @Autowired
    public RegisterationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({ "/register" })
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/registerError")
    public String registerError(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("emailError", true);
        return "register";
    }

    @PostMapping({ "/register" })
    public String register(@ModelAttribute("user") User user, BindingResult result, WebRequest request, Model model, RedirectAttributes attributes) {
        if (userService.userExists(user.getEmail())) {
            return "redirect:/registerError";
        }
        userService.save(user);
        System.out.println(user);
        return "login";
    }
}
