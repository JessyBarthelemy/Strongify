package com.jessy_barthelemy.strongify.customLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class TutorialLayout extends RelativeLayout{

    private int[] point;
    private int radius;
    private Paint paint;
    private PorterDuffXfermode porterDuffXfermode;

    public TutorialLayout(Context context) {
        super(context);
        init();
    }

    public TutorialLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TutorialLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setLayerType(LAYER_TYPE_HARDWARE, null);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        point = new int[2];

        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setXfermode(porterDuffXfermode);
        canvas.drawCircle(point[0], point[1], radius, paint);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setPoint(int[] point) {
        this.point = point;
        this.invalidate();
    }
}
