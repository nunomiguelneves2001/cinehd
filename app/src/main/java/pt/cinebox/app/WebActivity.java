package pt.cinebox.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebActivity extends Activity {

    // ====== CONFIGURAÇÃO ======
    private static final String START_URL = "https://cinehd.vc/";
    private static final String ALLOWED_HOST = "cinehd.vc";
    // true = bloqueia navegação para outros sites (anti-publicidade/redirecionamentos).
    // Se o site mudar de domínio e ficar em branco, põe false.
    private static final boolean BLOCK_EXTERNAL = true;
    // ==========================

    private FrameLayout root;
    private FrameLayout fullscreenContainer;
    private WebView web;
    private CursorView cursor;
    private ProgressBar progress;

    private View customView;
    private WebChromeClient.CustomViewCallback customCallback;

    private boolean cursorOn = true;
    private float cx;
    private float cy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        root = findViewById(R.id.root);
        fullscreenContainer = findViewById(R.id.fullscreen_container);
        web = findViewById(R.id.web);
        cursor = findViewById(R.id.cursor);
        progress = findViewById(R.id.progress);

        setupWebView();

        root.post(() -> {
            cx = root.getWidth() / 2f;
            cy = root.getHeight() / 2f;
            cursor.setCursor(cx, cy);
        });

        web.loadUrl(START_URL);
        Toast.makeText(this, "Botão menu ou vermelho: liga/desliga o cursor", Toast.LENGTH_LONG).show();
    }

    private void setupWebView() {
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setSupportMultipleWindows(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        // Pede a versão "ecrã grande" do site em vez da versão telemóvel
        String ua = s.getUserAgentString().replace("; wv", "").replace(" Mobile", "");
        s.setUserAgentString(ua);

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(web, true);

        web.setBackgroundColor(Color.BLACK);
        web.setFocusable(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                String scheme = u.getScheme();
                if (!"http".equals(scheme) && !"https".equals(scheme)) {
                    return true; // bloqueia intent://, market://, etc.
                }
                if (request.isForMainFrame() && BLOCK_EXTERNAL && !isAllowedHost(u.getHost())) {
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                customView = view;
                customCallback = callback;
                fullscreenContainer.addView(view, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                fullscreenContainer.setVisibility(View.VISIBLE);
                web.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onHideCustomView() {
                if (customView == null) return;
                fullscreenContainer.removeView(customView);
                fullscreenContainer.setVisibility(View.GONE);
                customView = null;
                web.setVisibility(View.VISIBLE);
                if (customCallback != null) {
                    customCallback.onCustomViewHidden();
                    customCallback = null;
                }
            }
        });
    }

    private boolean isAllowedHost(String host) {
        if (host == null) return false;
        return host.equals(ALLOWED_HOST) || host.endsWith("." + ALLOWED_HOST);
    }

    // ---------- Comando ----------

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        int code = e.getKeyCode();
        boolean down = e.getAction() == KeyEvent.ACTION_DOWN;

        if (code == KeyEvent.KEYCODE_BACK) {
            if (!down) handleBack();
            return true;
        }

        if (code == KeyEvent.KEYCODE_MENU || code == KeyEvent.KEYCODE_PROG_RED) {
            if (down && e.getRepeatCount() == 0) toggleCursor();
            return true;
        }

        if (code == KeyEvent.KEYCODE_PAGE_DOWN || code == KeyEvent.KEYCODE_CHANNEL_DOWN) {
            if (down && customView == null) web.scrollBy(0, web.getHeight() * 8 / 10);
            return true;
        }
        if (code == KeyEvent.KEYCODE_PAGE_UP || code == KeyEvent.KEYCODE_CHANNEL_UP) {
            if (down && customView == null) web.scrollBy(0, -web.getHeight() * 8 / 10);
            return true;
        }

        if (cursorOn) {
            switch (code) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (down) moveCursor(0, -1, e.getRepeatCount());
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (down) moveCursor(0, 1, e.getRepeatCount());
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (down) moveCursor(-1, 0, e.getRepeatCount());
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (down) moveCursor(1, 0, e.getRepeatCount());
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                    if (down && e.getRepeatCount() == 0) tap();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(e);
    }

    private void handleBack() {
        if (customView != null) {
            web.getWebChromeClient().onHideCustomView();
        } else if (web.canGoBack()) {
            web.goBack();
        } else {
            finish();
        }
    }

    private void toggleCursor() {
        cursorOn = !cursorOn;
        cursor.setCursorVisible(cursorOn);
        Toast.makeText(this, cursorOn ? "Cursor: LIGADO" : "Cursor: DESLIGADO (setas navegam pelos links)",
                Toast.LENGTH_SHORT).show();
    }

    private void moveCursor(int dx, int dy, int repeat) {
        float step = dp(14) * (1f + Math.min(repeat, 10) * 0.4f);
        float w = root.getWidth();
        float h = root.getHeight();
        cx = Math.max(0, Math.min(w - 1, cx + dx * step));
        cy = Math.max(0, Math.min(h - 1, cy + dy * step));

        // Perto da margem de cima/baixo: faz scroll da página
        float edge = dp(48);
        if (customView == null) {
            if (dy > 0 && cy > h - edge) web.scrollBy(0, (int) step);
            if (dy < 0 && cy < edge) web.scrollBy(0, (int) -step);
        }
        cursor.setCursor(cx, cy);
    }

    private void tap() {
        final View target = (customView != null) ? customView : web;
        final float x = cx;
        final float y = cy;
        final long t = SystemClock.uptimeMillis();

        MotionEvent d = MotionEvent.obtain(t, t, MotionEvent.ACTION_DOWN, x, y, 0);
        d.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        target.dispatchTouchEvent(d);
        d.recycle();

        target.postDelayed(() -> {
            long t2 = SystemClock.uptimeMillis();
            MotionEvent u = MotionEvent.obtain(t, t2, MotionEvent.ACTION_UP, x, y, 0);
            u.setSource(InputDevice.SOURCE_TOUCHSCREEN);
            target.dispatchTouchEvent(u);
            u.recycle();
        }, 60);
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    // ---------- Ciclo de vida ----------

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

    @Override
    protected void onPause() {
        super.onPause();
        web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.destroy();
        }
        super.onDestroy();
    }
}
