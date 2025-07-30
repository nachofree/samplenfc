package com.example.sample_with_sdk;

import android.graphics.Bitmap;
import android.graphics.Color;

public class b {
    private Bitmap input;

    public b(Bitmap bitmap) {
        this.input = bitmap;
    }

    public Bitmap a() {
        // Create output bitmap with same config as input
        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());

        int width = input.getWidth();
        int height = input.getHeight();

        // Process all pixels at once for better performance
        int[] pixels = new int[width * height];
        input.getPixels(pixels, 0, width, 0, 0, width, height);

        // Apply Floyd-Steinberg dithering for better image quality
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int pixel = pixels[index];

                // Extract RGB values
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                // Convert to grayscale using standard weights
                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);

                // Apply threshold
                int newPixel = gray < 128 ? 0xFF000000 : 0xFFFFFFFF;
                pixels[index] = newPixel;

                // Calculate error for dithering
                int error = gray - (newPixel == 0xFF000000 ? 0 : 255);

                // Distribute error to neighboring pixels (Floyd-Steinberg)
                if (x + 1 < width) {
                    int idx = index + 1;
                    int p = pixels[idx];
                    int gr = Color.red(p);
                    gr = Math.max(0, Math.min(255, gr + (error * 7 / 16)));
                    pixels[idx] = Color.rgb(gr, gr, gr);
                }

                if (y + 1 < height) {
                    if (x > 0) {
                        int idx = index + width - 1;
                        int p = pixels[idx];
                        int gr = Color.red(p);
                        gr = Math.max(0, Math.min(255, gr + (error * 3 / 16)));
                        pixels[idx] = Color.rgb(gr, gr, gr);
                    }

                    int idx = index + width;
                    int p = pixels[idx];
                    int gr = Color.red(p);
                    gr = Math.max(0, Math.min(255, gr + (error * 5 / 16)));
                    pixels[idx] = Color.rgb(gr, gr, gr);

                    if (x + 1 < width) {
                        idx = index + width + 1;
                        p = pixels[idx];
                        gr = Color.red(p);
                        gr = Math.max(0, Math.min(255, gr + (error * 1 / 16)));
                        pixels[idx] = Color.rgb(gr, gr, gr);
                    }
                }
            }
        }

        // Force final pixels to pure black or white
        for (int i = 0; i < pixels.length; i++) {
            int gray = Color.red(pixels[i]); // Since we made it grayscale
            pixels[i] = gray < 128 ? 0xFF000000 : 0xFFFFFFFF;
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height);
        return output;
    }
}