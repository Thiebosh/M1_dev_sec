package fr.yncrea.pyjabank;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        String str = "Ceci est un démonstrateur sur le thème de la sécurité\n" +
                "Cette application communique de façon sécurisée avec une api rest\n" +
                "Les données récupérées par TSL et Certificate pinning sont ensuite stockées en local, avec une bdd chiffrée\n" +
                "blablabla";

        ((TextView) findViewById(R.id.act_home_text_intro)).setText(str);
        str = null;

        findViewById(R.id.act_home_button_next).setOnClickListener(v ->
            startActivity((new Intent(HomeActivity.this, AppActivity.class))
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }
}
