package pt.cinebox.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/** Cursor desenhado por cima do site, movido com as setas do comando. */
public class CursorView extends View {

    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float radius;
    private float x;
    private float y;
    private boolean on = true;

    public CursorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float d = context.getResources().getDisplayMetrics().density;
        radius = 11 * d;
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(0xCCFFFFFF);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(2.5f * d);
        stroke.setColor(0xFF000000);
        setFocusable(false);
        setClickable(false);
    }

    public void setCursor(float x, float y) {
        this.x = x;
        this.y = y;
        invalidate();
    }

    public void setCursorVisible(boolean visible) {
        this.on = visible;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!on) return;
        canvas.drawCircle(x, y, radius, fill);
        canvas.drawCircle(x, y, radius, stroke);
    }
}
