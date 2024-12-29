package com.art.galary.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.art.galary.models.Artwork;
import com.art.galary.models.User;
import com.art.galary.repository.ArtworkRepository;
import com.art.galary.repository.UserRepository;

@Service
public class ArtworkServiceImpl implements ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;

    @Autowired
    public ArtworkServiceImpl(ArtworkRepository artworkRepository, UserRepository userRepository) {
        this.artworkRepository = artworkRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(Artwork artwork, User user) {
        artwork.setOwner_id(user.getId());
        artwork.setLikes(0);
        artwork.setLabel("Verifying");
        String imgUrl = "/img/artwork-photos/" + artwork.getId() + "/" + artwork.getImgUrl();
        artwork.setImgUrl(imgUrl);
        artworkRepository.save(artwork); // Save the artwork to the repository
    }

    @Override
    public List<Artwork> getArtworks() {
        return artworkRepository.findAllArtworks();
    }

    @Override
    public Artwork findArtworkById(int id) {
        return artworkRepository.findArtworkById(id);
    }

    @Override
    public List<Artwork> findArtworkByOwner(int id) {
        return artworkRepository.findArtworkByOwner(id);
    }

    @Override
    public void updateArtwork(Artwork artwork) {
        // Ensure the artwork is saved with updated details
        String imgUrl = "/img/artwork-photos/" + artwork.getId() + "/" + artwork.getImgUrl();
        artwork.setImgUrl(imgUrl);
        artworkRepository.save(artwork); // Save the updated artwork
    }

    @Override
    public void updateArtworkLikes(int id, int likes) {
        artworkRepository.updateArtworkLikes(id, likes);
    }

    @Override
    public String getArtOwnerName(Artwork artwork) {
        User user = userRepository.findByUserId(artwork.getOwner_id());
        return user.getFirstName();
    }
}
