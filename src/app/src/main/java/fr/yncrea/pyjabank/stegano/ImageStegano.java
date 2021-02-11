package fr.yncrea.pyjabank.stegano;

import android.content.Context;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;

import fr.yncrea.pyjabank.R;

public class ImageStegano {
    Bitmap mbitmap;

    public ImageStegano(Bitmap bitmap){
        mbitmap = bitmap;
    }


    public static byte[] tractopelle(Bitmap bitmap){ //transforme une bitmap en tableau de byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        bitmap.recycle();

        return byteArray;
    }

    public static String unsinagaz(byte[] input) {//transforme un tableau de byte en binaire

        StringBuilder result = new StringBuilder();
        for (byte b : input) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                result.append((val & 128) == 0 ? 0 : 1);      // 128 = 1000 0000
                val <<= 1;
            }
        }
        return result.toString();

    }

    public static byte[] loveuGaby( String info){ //transforme un string en tableau de byte
        byte[] infoBin = null;
        try {
            infoBin = info.getBytes( "UTF-8" );
        }
        catch (Exception e){
            //Toast.makeText(context,"An error occured in the convertion",Toast.LENGTH_SHORT);
        }
        return infoBin;
    }

    public static String binToString(byte[] array){ //transforme un tableau de byte en utf8

        String res = "";
        for (byte b : array) {
            res = res + (char) b;
        }
        return res;
    }

    public static String decrypt(Bitmap bitmap){
        int u = 0;

       int [] binarysecret = new int[144*3];
        for (int y = 2; y < bitmap.getHeight(); y = y + 33) {
            for (int x = 2; x < bitmap.getWidth(); x = x + 33) {

                int pixel = bitmap.getPixel(x,y);
                //Log.d("testy", "("+ Color.red(pixel)+","+Color.green(pixel)+","+Color.blue(pixel)+")");
                binarysecret[u] = convert(Color.red(pixel));
                binarysecret[u+1] = convert(Color.green(pixel));
                binarysecret[u+2] = convert(Color.blue(pixel));
                u += 3;
            }
        }
        return  binToString(binToByte(binarysecret));
    }

    private static int convert(int color){
        if(color == 0){
            return 0;
        }
        else{
            return 1;
        }
    }

    private static byte[] binToByte(int [] binary){
        String bin= "";
        String url = "";
        int u = 0;
        byte[] hey = new byte[48];
        for (int i = 0; i < 384; i += 8){
            for (int j = 0; j < 8; j++){
                bin += binary[i+j];

            }

            hey[u] = Byte.parseByte(bin, 2);
            u++;
            bin = "";
        }
        return hey;
    }

}
