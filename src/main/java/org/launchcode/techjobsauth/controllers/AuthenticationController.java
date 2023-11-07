package org.launchcode.techjobsauth.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.launchcode.techjobsauth.models.User;
import org.launchcode.techjobsauth.models.data.UserRepository;
import org.launchcode.techjobsauth.models.dto.RegisterFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AuthenticationController {
    @Autowired
    private UserRepository userRepository;

    private static final String userSession = "User";

    public User getUserFromSession (HttpSession session){
        Integer userId = (Integer) session.getAttribute(userSession);
        if (userId.equals(null)) {
            return null;
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()){
            return null;
        }
        return user.get();
    }

    private static void setUserFormSession (HttpSession session, User user){
        session.setAttribute(userSession, user.getId());
    }

    @GetMapping("/register")
    public String displayRegistrationForm (Model model){
        model.addAttribute(new RegisterFormDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistrationForm(@ModelAttribute @Valid RegisterFormDTO registerFormDTO, Errors errors, HttpServletRequest request){
        if(errors.hasErrors()){
            return "register";
        }
        User user = userRepository.findByUsername(registerFormDTO.getUsername());
        if(user != null) {
            errors.rejectValue("username", "username.alreadyexists", "This username already exists.");
            return "register";
        }
        String password = registerFormDTO.getPassword();
        String verifyPassword = registerFormDTO.getVerifyPassword();
        if(!password.equals(verifyPassword)){
            errors.rejectValue("password", "passwords.mismatch", "Passwords do not match.");
            return "register";
        }
        User newUser = new User(registerFormDTO.getUsername(), registerFormDTO.getPassword());
        userRepository.save(newUser);
        setUserFormSession(request.getSession(), newUser);
        return "redirect:";
    }
}
