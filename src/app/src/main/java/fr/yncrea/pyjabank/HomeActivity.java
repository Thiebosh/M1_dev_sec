package fr.yncrea.pyjabank;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import fr.yncrea.pyjabank.library.steganography.Text.AsyncTaskCallback.TextDecodingCallback;
import fr.yncrea.pyjabank.library.steganography.Text.AsyncTaskCallback.TextEncodingCallback;
import fr.yncrea.pyjabank.library.steganography.Text.ImageSteganography;
import fr.yncrea.pyjabank.library.steganography.Text.TextDecoding;
import fr.yncrea.pyjabank.library.steganography.Text.TextEncoding;


public class HomeActivity extends AppCompatActivity implements TextEncodingCallback, TextDecodingCallback {

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

        // test stegano lib
        ImageSteganography imageSteganography = new ImageSteganography("coucou", "key",
                BitmapFactory.decodeResource(getResources(), R.raw.testlib));

        TextEncoding textEncoding = new TextEncoding(HomeActivity.this, HomeActivity.this);

        textEncoding.execute(imageSteganography);
    }

    @Override
    public void onStartTextEncoding() {
        Log.d("testy", "start text encoding");
    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        Log.d("testy", "finish text encoding");

        if (result != null && result.isEncoded()) {

            Log.d("testy", "text encoded");

            //encrypted image bitmap is extracted from result object
            //encoded_image = result.getEncrypted_image();

            ImageSteganography imageSteganography = new ImageSteganography("key", result.getImage());
            TextDecoding textDecoding = new TextDecoding(HomeActivity.this, HomeActivity.this);
            textDecoding.execute(imageSteganography);
        }
    }

    @Override
    public void onStartTextDecoding() {
        Log.d("testy", "start text decoding");
    }

    @Override
    public void onCompleteTextDecoding(ImageSteganography result) {
        Log.d("testy", "finish text decoding");

        if (result != null) {
            /* If result.isDecoded() is false, it means no Message was found in the image. */
            if (!result.isDecoded()) Log.d("testy", "No message found");

            else{
                /* If result.isSecretKeyWrong() is true, it means that secret key provided is wrong. */
                if (result.isSecretKeyWrong()) Log.d("testy", "Wrong secret key");
                else Log.d("testy", "Decoded : " + result.getMessage());
            }
        }
        else {
            //If result is null it means that bitmap is null
            Log.d("testy", "Select Image First");
        }
    }
}
