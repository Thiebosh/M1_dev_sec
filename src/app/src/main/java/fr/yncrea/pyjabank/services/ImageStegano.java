package fr.yncrea.pyjabank.services;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class ImageStegano {

    public static String decrypt(final Resources res, final int imgID) {
        final double bitmapDimRatio = 2.75;
        final int bitmapDimFactor = 33;

        Bitmap bitmap = BitmapFactory.decodeResource(res, imgID);

        int u = 0;
        int[] binary = new int[(int) (bitmap.getWidth()/ bitmapDimRatio) * 3];//RGB
        for (int y = 2; y < bitmap.getHeight(); y += bitmapDimFactor) {
            for (int x = 2; x < bitmap.getWidth(); x += bitmapDimFactor) {
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
        final int nbByte = 8;

        StringBuilder res = new StringBuilder();
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < binary.length; i += nbByte) {
            StringBuilder bin = new StringBuilder();
            for (int j = 0; j < nbByte; ++j) bin.append(binary[i + j]);
            try {
                res.append((char) Byte.parseByte(bin.toString(), 2));
                tmp.append(bin.toString());
            }
            catch (Exception ignore) {
                break;
            }
        }

        return res.toString();
    }

}
