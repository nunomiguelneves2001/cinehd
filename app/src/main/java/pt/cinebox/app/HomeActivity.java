package pt.cinebox.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends Activity {

    private static final String URL_HOME = "https://cinehd.vc/";
    private static final String URL_MOVIES = "https://cinehd.vc/search?type=movie";
    private static final String URL_SERIES = "https://cinehd.vc/search?type=tv";
    private static final String URL_LIVE = "https://cinehd.vc/live-tv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final TextView title = findViewById(R.id.title);
        title.post(() -> {
            title.getPaint().setShader(new LinearGradient(0, 0, title.getWidth(), 0,
                    0xFFFF4FA3, 0xFFFFB347, Shader.TileMode.CLAMP));
            title.invalidate();
        });

        Button movies = findViewById(R.id.btn_movies);
        Button series = findViewById(R.id.btn_series);
        Button live = findViewById(R.id.btn_live);
        Button enter = findViewById(R.id.btn_enter);
        Button exit = findViewById(R.id.btn_exit);

        movies.setOnClickListener(v -> open(URL_MOVIES));
        series.setOnClickListener(v -> open(URL_SERIES));
        live.setOnClickListener(v -> open(URL_LIVE));
        enter.setOnClickListener(v -> open(URL_HOME));
        exit.setOnClickListener(v -> finish());

        View.OnFocusChangeListener grow = (v, hasFocus) ->
                v.animate().scaleX(hasFocus ? 1.06f : 1f).scaleY(hasFocus ? 1.06f : 1f).setDuration(150).start();
        for (Button b : new Button[]{movies, series, live, enter, exit}) {
            b.setOnFocusChangeListener(grow);
        }

        movies.requestFocus();
    }

    private void open(String url) {
        Intent i = new Intent(this, WebActivity.class);
        i.putExtra(WebActivity.EXTRA_URL, url);
        startActivity(i);
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
