package com.cloudlab.healthAi.qrcode;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

//reference: http://www.dilek.me/java/aws/lambda/2016/05/05/AWS-Lambda-Java-Example/

public class QrcodeHandler implements RequestHandler<String, String> {

    static {
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard();
        AWSXRay.setGlobalRecorder(builder.build());
    }

    @Override
    public String handleRequest(String url, Context context) {

        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);


        try {
            Path filePath = downloadImage(url);
            System.out.println("QR Code image created successfully!");
            String data = readQRCode(filePath);

            System.out.println("Data read from QR Code: "
                    + data);

            return data;
        } catch (NotFoundException e) {
            return "";
        } catch (IOException e) {
            return "decode error " + e;
        }
    }

    private Path downloadImage(String url) throws IOException {
        URL imageUrl = new URL(url);
        String filename = imageUrl.getFile().toLowerCase();
        String fileExtension = "";
        if (filename.contains("jpg") || filename.contains(".jpeg")) {
            fileExtension = "jpg";
        } else if (filename.contains(".png")) {
            fileExtension = "png";
        }

        Path filePath = Paths.get("/tmp/image." + fileExtension);
        if (Files.exists(filePath))
            Files.delete(filePath);
        try (InputStream in = imageUrl.openStream()) {
            Files.copy(in, filePath);
        }
        return filePath;
    }

    /**
     * @param filePath
     * @return Qr Code value
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NotFoundException
     */
    public static String readQRCode(Path filePath)
            throws IOException, NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(new FileInputStream(filePath.toString())))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
        return qrCodeResult.getText();
    }

}