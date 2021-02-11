package fr.yncrea.pyjabank;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        String str = String.join("\n", Arrays.asList(getResources().getStringArray(R.array.act_home_text_intro)));
        ((TextView) findViewById(R.id.act_home_text_intro)).setText(str);

        findViewById(R.id.act_home_button_next).setOnClickListener(v ->
            startActivity((new Intent(HomeActivity.this, AppActivity.class))
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        /*byte[] var = new ImageStegano(BitmapFactory.decodeResource(getResources(), R.drawable.out)).tractopelle();
        Log.d("testy", ImageStegano.unsinagaz(var));
        Log.d("testy", ImageStegano.binToString(var));*/

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.oute);
        Log.d("testy", bitmap.getWidth()+" "+bitmap.getHeight()+"");

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];

        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, 12, 12);

        StringBuilder tmp = new StringBuilder();
        for (int elt: pixels) {
            tmp.append(elt).append(" ");
        }
        Log.d("testy", tmp.toString());

        /*Log.d("testy", bitmap.getWidth()+" * "+bitmap.getHeight());
        for (int y = 0; y < bitmap.getHeight(); ++y) {
            for (int x = 0; x < bitmap.getWidth(); ++x) {
                int pixel = bitmap.getPixel(x,y);
                Log.d("testy", "("+Color.red(pixel)+","+Color.blue(pixel)+","+Color.green(pixel)+")");
            }
            Log.d("testy", "newline");
        }*/

        //((0,255,255),(0,255,0),(0,0,0),(255,255,255),(0,255,0),(0,0,255),(255,255,0),(255,0,0),(0,255,255),(255,0,0),(0,0,0),(255,255,255),(0,0,255),(255,0,0),(255,255,255),(0,255,0),(0,0,255),(0,255,255),(255,255,0),(0,255,0),(255,255,255),(255,0,0),(255,255,0),(255,255,0),(0,0,255),(255,0,0),(0,0,0),(0,255,255),(0,0,0),(0,0,0),(255,255,0),(255,255,255),(0,255,255),(0,0,255),(255,0,0),(0,255,255),(0,0,0),(255,0,255),(255,0,0),(0,0,255),(0,0,255),(255,0,255),(0,0,0),(0,255,255),(0,0,255),(255,0,0),(255,255,0),(0,0,0),(0,0,255),(255,255,0),(0,255,0),(255,255,0),(0,255,255),(0,0,0),(255,255,255),(0,0,0),(0,255,255),(0,0,0),(255,0,0),(0,255,255),(0,0,0),(0,0,0),(255,255,0),(0,0,0),(0,0,255),(255,0,0),(0,255,0),(0,255,255),(0,255,255),(255,0,255),(255,0,0),(255,0,255),(0,255,255),(0,0,255),(0,255,0),(0,255,255),(0,255,0),(255,0,0),(255,255,0),(0,0,0),(0,0,255),(255,0,0),(255,0,0),(0,255,255),(0,0,255),(0,0,0),(255,0,255),(255,255,0),(0,255,255),(0,255,255),(0,255,0),(255,255,0),(255,255,255),(255,0,255),(255,0,0),(0,255,255),(0,255,255),(0,255,0),(255,255,0),(255,255,0),(0,0,0),(255,0,255),(255,255,0),(0,0,0),(0,255,255),(0,255,0),(0,255,0),(0,255,0),(255,255,255),(0,0,255),(255,0,255),(0,0,255),(0,255,255),(0,255,255),(255,255,0),(0,255,0),(255,255,255),(255,0,255),(255,0,0),(0,0,255),(0,255,255),(255,0,0),(0,0,0),(255,255,0),(255,0,0),(255,0,0),(255,0,255),(255,255,255))

        //Log.d("testy", getBitmapPixels(bitmap, 0,0, 12,12).length+"");
    }


    public static int[] getBitmapPixels(Bitmap bitmap, int x, int y, int width, int height) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), x, y,
                width, height);
        final int[] subsetPixels = new int[width * height];
        for (int row = 0; row < height; row++) {
            System.arraycopy(pixels, (row * bitmap.getWidth()),
                    subsetPixels, row * width, width);
        }
        return subsetPixels;
    }

}
