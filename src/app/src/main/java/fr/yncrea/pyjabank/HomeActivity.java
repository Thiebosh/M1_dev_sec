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

import fr.yncrea.pyjabank.stegano.ImageStegano;


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


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.resolution);
        String url = ImageStegano.decrypt(bitmap);

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
