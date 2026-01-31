package telekocsi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import telekocsi.model.Ride;
import telekocsi.model.User;
import telekocsi.service.RideService;
import telekocsi.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final RideService rideService;

    public UserController(UserService userService, RideService rideService) {
        this.userService = userService;
        this.rideService = rideService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/users/new")
    public String showUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("title", "Add New User (Admin)");
        return "user_form";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id);
        user.setPassword("");
        model.addAttribute("user", user);
        model.addAttribute("title", "Edit User");
        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUserByAdmin(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            boolean onlyPasswordError = bindingResult.getFieldErrors().stream()
                    .allMatch(err -> err.getField().equals("password") && user.getId() != null);

            if (!onlyPasswordError) {
                model.addAttribute("title", user.getId() == null ? "Add New User" : "Edit User");
                return "user_form";
            }
        }

        try {
            userService.saveUserByAdmin(user);
            redirectAttributes.addFlashAttribute("successMessage", "User saved successfully!");
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("title", user.getId() == null ? "Add New User" : "Edit User");
            return "user_form";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            userService.deleteUserById(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = userService.findByUserName(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Ride> myRides = rideService.findRidesByDriver(user);
        List<Ride> joinedRides = rideService.findRidesByPassenger(user);

        user.setPassword("");
        model.addAttribute("user", user);
        model.addAttribute("myRides", myRides);
        model.addAttribute("joinedRides", joinedRides);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute User user, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal) {
        User currentUser = userService.findByUserName(principal.getName()).orElseThrow();

        if (!currentUser.getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized action.");
            return "redirect:/profile";
        }

        if (bindingResult.hasErrors()) {
            boolean onlyPasswordError = bindingResult.getFieldErrors().stream()
                    .allMatch(err -> err.getField().equals("password"));
            if (!onlyPasswordError) {
                return "profile";
            }
        }

        try {
            user.setRole(currentUser.getRole());
            user.setUsername(currentUser.getUsername());

            userService.saveUserByAdmin(user);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteMyProfile(Principal principal, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUserName(principal.getName()).orElseThrow();
            userService.deleteUserById(user.getId(), "");

            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            redirectAttributes.addFlashAttribute("successMessage", "Account deleted successfully.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile";
        }
    }
}