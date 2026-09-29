package com.skbank.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class TransparentLogoMaker {

    public static void main(String[] args) {
        String[] imagePaths = {
            "src/main/webapp/images/sk-bank-logo-transparent.png",
            "src/main/webapp/images/sk-bank-logo.png",
            "src/main/webapp/assets/images/sk-bank-logo-transparent.png",
            "src/main/webapp/assets/images/sk-bank-logo.png",
            "app/src/main/res/drawable/sk_bank_logo.png",
            "app/src/main/res/drawable-nodpi/sk_bank_logo.png"
        };

        for (String path : imagePaths) {
            processImage(new File(path));
        }
    }

    private static void processImage(File file) {
        if (!file.exists()) {
            System.out.println("File not found: " + file.getAbsolutePath());
            return;
        }

        try {
            BufferedImage src = ImageIO.read(file);
            int width = src.getWidth();
            int height = src.getHeight();

            BufferedImage transparentImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            double cx = width / 2.0;
            double cy = height / 2.0;
            double maxRadius = Math.min(width, height) / 2.0 - 4; // Outer boundary of golden circle

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = src.getRGB(x, y);
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;

                    double dist = Math.hypot(x - cx, y - cy);

                    // Check if pixel is outside golden circle or belongs to grey checkerboard background
                    boolean isGreyBackground = (Math.abs(r - g) < 15 && Math.abs(g - b) < 15 && Math.abs(r - b) < 15 && r > 100 && r < 230);
                    
                    if (dist > maxRadius || (dist > maxRadius - 15 && isGreyBackground)) {
                        // Make pixel 100% fully transparent
                        transparentImg.setRGB(x, y, 0x00000000);
                    } else {
                        // Keep original pixel
                        transparentImg.setRGB(x, y, argb);
                    }
                }
            }

            ImageIO.write(transparentImg, "png", file);
            System.out.println("Successfully made background 100% transparent for: " + file.getPath());

        } catch (Exception e) {
            System.err.println("Error processing " + file.getName() + ": " + e.getMessage());
        }
    }
}
