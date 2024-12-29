package com.art.galary.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.art.galary.models.Artwork;
import com.art.galary.models.User;
import com.art.galary.services.ArtworkService;
import com.art.galary.services.SecurityService;
import com.art.galary.services.UserService;

import java.io.IOException;

@Controller
public class AddArtController {

    private final ArtworkService artworkService;
    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public AddArtController(ArtworkService artworkService, UserService userService, SecurityService securityService) {
        this.artworkService = artworkService;
        this.userService = userService;
        this.securityService = securityService;
    }

    // Show the form to add or update an artwork
    @GetMapping("/addArt")
    public String addArt(Model model, @RequestParam(value = "artworkId", required = false) Integer artworkId) {
        if (artworkId != null) {
            Artwork artwork = artworkService.findArtworkById(artworkId);
            model.addAttribute("artwork", artwork);
        } else {
            model.addAttribute("artwork", new Artwork());
        }
        return "addArt";
    }

    // Handle form submission to either save a new artwork or update an existing one
    @PostMapping("/addArt")
    public String saveOrUpdateArt(@ModelAttribute("artwork") Artwork artwork, 
                                  @RequestParam("image") MultipartFile multipartFile, 
                                  Model model) {
        try {
            int currentUserId = userService.findByUsername(securityService.findLoggedInUsername()).getId();
            User user = userService.findByUserId(currentUserId);

            // Check if the artwork already exists by its id (Integer can be null)
            String fileName = multipartFile.getOriginalFilename();
            artwork.setImgUrl("/img/artwork-photos/" + fileName); // Set the image URL

            // If artwork.getId() is null, it's a new artwork, else it's an update
            if (artwork.getId() != null) { // Update the existing artwork
                artworkService.updateArtwork(artwork);
            } else { // Save the new artwork
                artworkService.save(artwork, user);
            }

            // Save the uploaded image to the directory
            String uploadDir = "src/main/resources/static/img/artwork-photos/" + artwork.getId(); // Path for saving image
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            return "redirect:/shop"; // Redirect to the shop page after adding or updating artwork
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error occurred while adding or updating the artwork.");
            return "addArt"; // Return to the form with an error message
        }
    }

    // Helper class for saving the uploaded file
    private static class FileUploadUtil {
        public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }
            try (java.io.InputStream inputStream = multipartFile.getInputStream()) {
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                java.nio.file.Files.copy(inputStream, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Could not save image file: " + fileName, ioe);
            }
        }
    }
}
