package fr.yncrea.pyjabank.stegano;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import fr.yncrea.pyjabank.R;

public class ImageStegano {
    Bitmap mbitmap;

    public ImageStegano(Bitmap bitmap){
        mbitmap = bitmap;
    }

    public byte[] tractopelle(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        mbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        mbitmap.recycle();
        for (Byte bytes : byteArray){
            Log.d("testy", String.valueOf(bytes + 128));

        }
        return byteArray;
    }

    public static String unsinagaz(byte[] input) {

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

    public static byte[] loveuGaby(Context context, String info){
        byte[] infoBin = null;
        try {
            infoBin = info.getBytes( "UTF-8" );
        }
        catch (Exception e){
            Toast.makeText(context,"An error occured in the convertion",Toast.LENGTH_SHORT);
        }
        return infoBin;
    }

    public static String binToString(byte[] array){

        String res = "";
        for (byte b : array) {
            res = res + (char) b;
        }
        return res;
    }


}
