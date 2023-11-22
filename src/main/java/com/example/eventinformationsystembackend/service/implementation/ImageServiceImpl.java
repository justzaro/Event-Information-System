package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.service.ImageService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public void createResizedImage(String originalImagePath,
                                   String resizedImagePath,
                                   String imageExtension) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(originalImagePath));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            BufferedImage resizedImage = resizeImage(originalImage, type, 200, 120);
            ImageIO.write(resizedImage, imageExtension, new File(resizedImagePath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage,
                                     int type, int IMG_WIDTH, int IMG_HEIGHT) {

        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D graphics = resizedImage.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        graphics.dispose();

        return resizedImage;
    }
}
