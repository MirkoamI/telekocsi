package telekocsi.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import telekocsi.model.Ride;
import telekocsi.model.Role;
import telekocsi.model.User;
import telekocsi.service.RideService;
import telekocsi.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class RideController {

    private final RideService rideService;
    private final UserService userService;

    public RideController(RideService rideService, UserService userService) {
        this.rideService = rideService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String viewHomePage(@RequestParam(defaultValue = "0") int page, Model model,
                               @RequestParam(required = false) String keyword) {
        List<Ride> allRides = rideService.getAllRides();

        System.out.println(">>> RIDES FOUND: " + allRides.size());

        model.addAttribute("rides", allRides);
        model.addAttribute("totalRides", allRides.size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        return "index";
    }


    @PostMapping("/ride/{id}/leave")
    public String leaveRide(@PathVariable long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            User passenger = userService.findByUserName(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            rideService.removePassengerFromRide(id, passenger.getId());

            redirectAttributes.addFlashAttribute("successMessage", "You have left the ride.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ride/" + id;
    }
    @GetMapping("/search")
    public String searchRides(@RequestParam(required = false) String from,
                              @RequestParam(required = false) String to,
                              @RequestParam(required = false) String date,
                              Model model) {
        List<Ride> results = rideService.searchRides(from, to, date);
        model.addAttribute("rides", results);
        model.addAttribute("totalRides", rideService.getAllRides().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());

        model.addAttribute("searchFrom", from);
        model.addAttribute("searchTo", to);
        model.addAttribute("searchDate", date);

        return "index";
    }

    @GetMapping("/new-ride")
    public String showRideForm(Model model, @AuthenticationPrincipal User currentUser) {
        Ride ride = new Ride();
        if (currentUser.getRole() == Role.ADMIN) {
            model.addAttribute("drivers", userService.getPotentialDriversForAdmin(currentUser.getUsername()));
        } else {
            ride.setDriver(currentUser);
        }
        model.addAttribute("ride", ride);
        model.addAttribute("title", "Post a New Ride");
        return "ride_form";
    }

    @PostMapping("/ride/save")
    public String saveRide(@Valid @ModelAttribute Ride ride, BindingResult bindingResult,
                           Model model, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal User currentUser) {
        if (bindingResult.hasErrors()) {
            if (currentUser.getRole() == Role.ADMIN) {
                model.addAttribute("drivers", userService.getPotentialDriversForAdmin(currentUser.getUsername()));
            }
            return "ride_form";
        }
        try {
            if (currentUser.getRole() != Role.ADMIN) {
                ride.setDriver(currentUser);
            }
            rideService.saveRide(ride);
            redirectAttributes.addFlashAttribute("successMessage", "Ride saved successfully!");
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "ride_form";
        }
    }

    @GetMapping("/ride/{id}")
    public String getRideDetails(@PathVariable Long id, Model model) {
        model.addAttribute("ride", rideService.findRideById(id));
        return "ride_details";
    }

    @PostMapping("/ride/{id}/join")
    public String joinRide(@PathVariable long id, @RequestParam String username, RedirectAttributes redirectAttributes) {
        try {
            User passenger = userService.findByUserName(username).orElseThrow();
            rideService.addPassengerToRide(id, passenger.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Joined ride!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ride/" + id;
    }

    @GetMapping("/edit/{id}")
    public String showEditRideForm(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        Ride ride = rideService.findRideById(id);
        model.addAttribute("ride", ride);
        if (currentUser.getRole() == Role.ADMIN) {
            model.addAttribute("drivers", userService.getPotentialDriversForAdmin(currentUser.getUsername()));
        }
        model.addAttribute("title", "Edit Ride");
        return "ride_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteRide(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rideService.deleteRideById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Ride deleted.");
        return "redirect:/";
    }
}