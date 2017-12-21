package eu.securityproject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import eu.securityproject.beans.User;

/**
 * Created by Letizia Vitari on 20/12/17.
 */
public class QRCodeManagement {

    static final String SHA256 = "sha256";
    static final String period = "20";
    static final String digit = "8";
    static final String MySecurityProject = "MySecurityProject";

    public static void createQRCodeImage(User user) throws IOException, WriterException {

       BufferedImage image = generateBufferedImage(composeOtpPath(user));

        File outputfile = new File(user.getUsername() + ".png");
        ImageIO.write(image, "png", outputfile);

    }


    private static BufferedImage generateBufferedImage(String qrCodeStr) throws WriterException {

        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeStr, BarcodeFormat.QR_CODE, 150, 150, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        return image;
    }


    private static String composeOtpPath(User user){

        StringBuffer qrCodeText = new StringBuffer("otpauth://totp/").append(user.getUsername())
                .append("?secret=").append(user.getSecret())
                .append("&issuer=").append(MySecurityProject)
                .append("&algorithm=").append(SHA256)
                .append("&digits=").append(digit)
                .append("&period=").append(period)
                ;
        //System.out.println("qrCodeString " + qrCodeText.toString());
        return qrCodeText.toString();
    }



}
