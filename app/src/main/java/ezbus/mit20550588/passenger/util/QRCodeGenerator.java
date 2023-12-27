package ezbus.mit20550588.passenger.util;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {
    public static Bitmap generateMoreCustomisedQRCode(String data, int width, int height) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[y * width + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (WriterException e) {
            Log("QRCodeGenerator", "Error generating QR code", e.getMessage());
            return null;
        }
    }


    public static Bitmap generateQRCode(String data, int width, int height) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException e) {
            Log("QRCodeGenerator", "Error generating QR code", e.getMessage());
            return null;
        }
    }


}
