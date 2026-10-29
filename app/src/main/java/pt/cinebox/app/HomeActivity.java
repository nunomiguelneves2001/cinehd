package pt.cinebox.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button enter = findViewById(R.id.btn_enter);
        Button exit = findViewById(R.id.btn_exit);

        enter.setOnClickListener(v -> startActivity(new Intent(this, WebActivity.class)));
        exit.setOnClickListener(v -> finish());

        View.OnFocusChangeListener grow = (v, hasFocus) ->
                v.animate().scaleX(hasFocus ? 1.08f : 1f).scaleY(hasFocus ? 1.08f : 1f).setDuration(150).start();
        enter.setOnFocusChangeListener(grow);
        exit.setOnFocusChangeListener(grow);

        enter.requestFocus();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
