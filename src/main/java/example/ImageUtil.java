package example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

class ImageUtil {
    public static void main(String[] args) throws IOException {
        if(args.length != 2){
            System.out.println("Usage: ImageUtil input_file output_file");
            return;
        }

        String inputFilename = args[0];
        String outputFilename = args[1];
        System.out.println("resizing " + inputFilename + " to " + outputFilename);

        BufferedImage bImage = ImageIO.read(new File(inputFilename));
        bImage = resizeImage(bImage, 500);
        ImageIO.write(bImage, "jpeg", new File(outputFilename));
    }

    static BufferedImage resizeImage(BufferedImage bImage, int newWidth) {
        int width = bImage.getWidth();
        int height = bImage.getHeight();
        int newHeight = newWidth * height / width;

        System.out.printf(
                "resizeImage: (%d, %d) -> (%d, %d)\n",
                width, height, newWidth, newHeight
        );

        Image image = bImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage newBImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        newBImage.getGraphics().drawImage(image, 0, 0, null);

        return newBImage;
    }

    static byte[] bufferedImageToBytes(BufferedImage bImage, String formatName) throws IOException {
        bImage.flush();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bImage, formatName, os);
        return os.toByteArray();
    }
}
