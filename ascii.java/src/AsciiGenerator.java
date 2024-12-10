import java.io.File;    // to access file and directory paths
import java.io.FileWriter;  // to write to a file
import java.io.IOException;     // to handle errors
import javax.swing.*;   // to allow the user to choose from their directories
import javax.swing.filechooser.FileNameExtensionFilter;     // to restrict file type

import java.awt.image.BufferedImage;    // allows for pixel access to images
import javax.imageio.ImageIO;   // to read/write to the image
import java.awt.Color;      // access color information for each pixel
import java.awt.Graphics2D;     // to scale the image
import java.awt.Image;      // to create image

public class AsciiGenerator {
    public static void main(String[] args) throws IOException {
        JFileChooser fileChooser = new JFileChooser();      // Create a file chooser
        fileChooser.setCurrentDirectory(new File("/Users/ameliachen/Documents/egmt/art"));

        // Set the file filter to accept only image files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files",
        "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);

        // Show the file chooser dialog
        int result = fileChooser.showOpenDialog(null);

        // Check if the user clicked the "Open" button
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File selectedFile = fileChooser.getSelectedFile();
            BufferedImage image = ImageIO.read(selectedFile);

            int maxWidth = 300;

            // Calculate the corresponding height based on the aspect ratio
            int newWidth = maxWidth;
            int newHeight = (int) ((double) image.getHeight() * maxWidth / image.getWidth());

            // Resize the original image
            Image resizedImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.drawImage(resizedImage, 0, 0, null);
            g2d.dispose();

            String filepath = removeExtension(selectedFile.getAbsolutePath());

            convertToAscii(scaledImage, filepath);
        } else {
            System.out.println("No file selected");
        }
    }

    // Method to remove the file type extension from the file path
    private static String removeExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return filePath.substring(0, lastDotIndex);
        }
        return filePath;
    }

    public static Color[][] getArray(BufferedImage img){
        Color[][] arr;
        int numcols = img.getWidth();
        int numrows = img.getHeight();
        arr = new Color[numrows][numcols];
        for(int j = 0; j < arr.length; j++){
            for(int k = 0; k < arr[0].length; k++){
                int rgb = img.getRGB(k,j);
                arr[j][k] = new Color(rgb);
            }
        }
        return arr;
    }

    public static void grayScale(Color[][] arr){
        for(int j = 0; j < arr.length; j++){
            for(int k = 0; k < arr[0].length; k++){
                Color tmp = arr[j][k];
                int avg = (int)((tmp.getRed() +
                tmp.getGreen() + tmp.getBlue())/3);
                arr[j][k] = new Color(avg, avg, avg);
            }
        } 
    }

    public static void CreateFile(String filepath) {
        try {
            File asciiFile = new File(filepath + ".txt");
            if (asciiFile.createNewFile()) {
                System.out.println("File created: " + asciiFile.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void convertToAscii(BufferedImage img, String filename){
        Color[][] imgArray = getArray(img);
        grayScale(imgArray);
        char[] asciiChars = {' ', '.', '-', '~', '+', '<', '*', '$', '#', '@'};
        try{
            FileWriter writer = new FileWriter(filename + ".txt");
            for(int j = 0; j < imgArray.length; j++){
                for(int k = 0; k < imgArray[0].length; k++){
                    Color tmp = imgArray[j][k];
                    int index = (int) (tmp.getRed() / 255.0 * (asciiChars.length - 1));
                    writer.write(asciiChars[index]);
                }
                writer.write("\n");
            }
            writer.close();
        } catch  (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

