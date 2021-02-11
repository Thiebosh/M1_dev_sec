package fr.yncrea.pyjabank.services;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class ImageStegano {

    public static String decrypt(final Resources res, final int imgID) {
        Bitmap bitmap = BitmapFactory.decodeResource(res, imgID);

        int u = 0;
        int[] binary = new int[144*3];
        for (int y = 2; y < bitmap.getHeight(); y += 33) {
            for (int x = 2; x < bitmap.getWidth(); x += 33) {
                int pixel = bitmap.getPixel(x,y);

                binary[u++] = convert(Color.red(pixel));
                binary[u++] = convert(Color.green(pixel));
                binary[u++] = convert(Color.blue(pixel));
            }
        }

        return  binToString(binary);
    }

    private static int convert(int color) {
        return (color == 0 ? 0 : 1);
    }

    private static String binToString(int [] binary){
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < 384; i += 8) {
            StringBuilder bin = new StringBuilder();
            for (int j = 0; j < 8; ++j) bin.append(binary[i + j]);
            res.append((char) Byte.parseByte(bin.toString(), 2));
        }

        return res.toString();
    }

}
