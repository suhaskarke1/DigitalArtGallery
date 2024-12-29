package com.art.galary.controllers;

import java.sql.Timestamp;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.art.galary.models.Artwork;
import com.art.galary.models.Orders;
import com.art.galary.models.User;
import com.art.galary.services.ArtworkService;
import com.art.galary.services.OrdersService;
import com.art.galary.services.SecurityService;
import com.art.galary.services.UserService;

@Controller
public class OrderController {

    private final UserService userService;
    private final SecurityService securityService;
    private final OrdersService ordersService;
    private final ArtworkService artworkService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    public OrderController(UserService userService, SecurityService securityService, OrdersService ordersService, ArtworkService artworkService) {
        this.userService = userService;
        this.securityService = securityService;
        this.ordersService = ordersService;
        this.artworkService = artworkService;
    }

    @PostMapping("/order")
    public String addOrder(@ModelAttribute("orders") Orders orders, @RequestParam("artwork_id") int artwork_id, Model model) {
        model.addAttribute("loggedIn", securityService.isLoggedIn());

        // Get current user
        int currentUserId = userService.findByUsername(securityService.findLoggedInUsername()).getId();
        User user = userService.findByUserId(currentUserId);
        System.out.println(user);

        // Fetch the artwork by ID
        Artwork artwork = artworkService.findArtworkById(artwork_id);
        if (artwork == null) {
            model.addAttribute("errorMessage", "Artwork not found");
            return "error"; // Handle artwork not found scenario
        }
        int price = artwork.getPrice();
        orders.setPrice(price);
        System.out.println(price);

        // Set the ordered time
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        ts.setTime(1000 * (long) Math.floor(ts.getTime() / 1000));
        orders.setOrdered_at(ts);
        System.out.println(ts);

        // Update the artwork if necessary
        artworkService.updateArtwork(artwork);

        // Save the order and send mail
        ordersService.save(orders, user, artwork);

        // Sending mail
        String from = "suhaskarke410@gmail.com";
        String to = user.getEmail();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Your order for Artwork from Infinity Art Gallery");
        message.setText("Hello " + user.getFirstName() + "! \n" +
                        "Thanks for your order #" + orders.getId() + " placed on " + orders.getOrdered_at() +
                        " with Infinity Art Gallery. One of the best artwork from our gallery is headed your way! \n" +
                        "\n" + "Your Order total is " + orders.getPrice() + "$. We accept payment via cheque/debit/credit card. " +
                        "Simply reply to this mail to let us know how you wish to pay. We will send you a mail for further proceedings. " +
                        "If you wish to cancel the order, let us know via replying to this mail. The due date for the payment is upto " +
                        "15 days after receiving this mail. After that we may have to cancel your order. \n" +
                        "\n" + "We love your choice of this masterpiece! If you have any queries, just reply to this mail and we'll be " +
                        "right back to you!\n\nSincerely, \nInfinity Art Gallery");
        javaMailSender.send(message);

        return "redirect:/singleArt";
    }
}
