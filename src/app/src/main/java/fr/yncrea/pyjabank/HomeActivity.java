package fr.yncrea.pyjabank;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    }

}
