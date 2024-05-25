package ru.fotoochkarik.expensebot.qr;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QRTools {

  public static String getTextFromQR(String url) throws IOException {
    Result result;
    try {
      result = decodeBitmap(getBitmapFromUrl(url));
    } catch (RuntimeException | MalformedURLException e) {
      log.error("decodeQR: {}", e.getMessage());
      throw new IOException(String.format("Unable to decrypt QR-code: %s", e.getMessage()));
    }
    return result.getText();
  }

  private static BinaryBitmap getBitmapFromUrl(String url) throws IOException {
    BinaryBitmap binaryBitmap;
    try {
      binaryBitmap = new BinaryBitmap(new HybridBinarizer(
          new BufferedImageLuminanceSource(ImageIO.read(new URL(url)))));
    } catch (IOException e) {
      log.error("{QRTools.getBitmapFromUrl}: {}", e.getMessage());
      throw new IOException(String.format("Unable to decrypt QR-code: %s", e.getMessage()));
    }
    return binaryBitmap;
  }

  private static Result decodeBitmap(BinaryBitmap binaryBitmap) {
    Result result;
    try {
      result = new MultiFormatReader().decode(binaryBitmap);
    } catch (NotFoundException e) {
      log.error("Image does not contain QR-code: {}", e.getMessage());
      throw new IllegalArgumentException(String.format("Image does not contain QR-code: %s", e.getMessage()));
    }
    return result;
  }

}
