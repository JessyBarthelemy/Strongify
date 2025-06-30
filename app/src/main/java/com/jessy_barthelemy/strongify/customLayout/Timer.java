package com.jessy_barthelemy.strongify.customLayout;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.interfaces.OnTimerFinishedListener;
import com.jessy_barthelemy.strongify.service.ExecutionService;

import java.io.IOException;

public class Timer extends View{

    private Paint paint;
    private Paint textPaint;
    private RectF rect;
    private float stroke;
    private float textSize;

    private boolean silent;
    private int totalRest;
    private double restRadius;
    private int rest;
    private double step;
    private int color;
    private int backColor;
    private int backgroundColor;
    private Handler handler;
    private MediaPlayer player;
    //Number of tick to play before end
    private int tickCount;
    private Runnable runnable;
    private MediaPlayer tickPlayer;
    private AssetFileDescriptor tickSound;

    private int textX;
    private int textY;
    private long endTime;
    private ExecutionService service;
    private OnTimerFinishedListener onTimerFinishedListener;

    public Timer(Context context) {
        super(context);
        color = ContextCompat.getColor(getContext(), R.color.colorAccent);
        backColor = ContextCompat.getColor(getContext(), R.color.disabled);
        backgroundColor = ApplicationHelper.getThemeColor(getContext(), android.R.attr.windowBackground);
        stroke = 20;
        textSize = 40 * getResources().getDisplayMetrics().scaledDensity;

        init();
    }

    public Timer(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.Timer);
        color = array.getColor(R.styleable.Timer_circleColor, 0);
        backColor = array.getColor(R.styleable.Timer_circleBackColor, 0);
        backgroundColor = array.getColor(R.styleable.Timer_circleBackground, 0);
        stroke = array.getDimension(R.styleable.Timer_circleStroke, 20);
        textSize = array.getDimension(R.styleable.Timer_textSize, 40);

        array.recycle();
        init();
    }

    public Timer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        paint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(color);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        rect = new RectF();

        tickCount = ApplicationHelper.getTickCount(getContext());
        if(!silent && tickCount > 0){
            tickPlayer = MediaPlayer.create(getContext(), R.raw.tick);
            tickSound = getResources().openRawResourceFd(R.raw.tick);
            player = MediaPlayer.create(getContext(), R.raw.final_tick);
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                rest = (rest == 0) ? 0 : rest -1;
                restRadius -= step;
                invalidate();

                if(!silent && service != null && service.isStarted)
                    service.updateNotificationTitle(rest);
                if(rest > 0){
                    if(!silent && rest <= tickCount){
                        ApplicationHelper.makeVibration(getContext());
                        tickPlayer.reset();
                        try {
                            tickPlayer.setDataSource(tickSound.getFileDescriptor(), tickSound.getStartOffset(), tickSound.getDeclaredLength());
                            tickPlayer.prepare();
                        } catch (IOException ignore) {}
                        tickPlayer.start();
                    }

                    if(handler != null)
                        handler.postDelayed(this, 1000);
                }
                else{
                    if(!silent && player != null){

                        Handler lastSoundHandler = new Handler();
                        lastSoundHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ApplicationHelper.makeVibration(getContext());
                                final MediaPlayer finalPlayer = MediaPlayer.create(getContext(), R.raw.final_tick);
                                finalPlayer.start();
                                finalPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        finalPlayer.release();
                                    }
                                });
                            }
                        });

                        if(onTimerFinishedListener != null)
                            onTimerFinishedListener.onTimerFinished();
                    }else{
                        if(onTimerFinishedListener != null)
                            onTimerFinishedListener.onTimerFinished();
                    }

                    if(handler != null)
                        handler.removeCallbacks(runnable);
                }
            }
        };

        handler = new Handler();
    }

    public void start(){
        handler.postDelayed(runnable, 500);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.reset();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        rect.top = stroke;
        rect.left = stroke;

        rect.bottom = getHeight()-stroke;
        rect.right = getWidth()-stroke;


        if(backgroundColor != 0){
            paint.setColor(backgroundColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawOval(rect, paint);
        }

        paint.setColor(backColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        canvas.drawArc(rect, -90, 360, false, paint);

        paint.setColor(color);
        canvas.drawArc(rect, -90, (int)restRadius, false, paint);

        textX = canvas.getWidth() / 2;
        textY = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;

        canvas.drawText(String.valueOf(rest), textX, textY, textPaint);
    }

    public int getRest() {
        return rest;
    }

    public int getTotalRest() {
        return totalRest;
    }

    public void setRest(long endTime, int totalRest) {
        this.endTime = endTime;
        this.totalRest = totalRest;
        this.rest = (int)(endTime - System.currentTimeMillis()) / 1000;

        step = 360d / totalRest;
        this.restRadius = (int)(360 - (step * (totalRest - rest)));
    }

    public void setOnTimerFinishedListener(OnTimerFinishedListener onTimerFinishedListener) {
        this.onTimerFinishedListener = onTimerFinishedListener;
    }

    public void setService(ExecutionService service) {
        this.service = service;
    }

    public void onDestroy(){
        if(tickPlayer != null)
            tickPlayer.release();
        if(player != null)
            player.release();
        if(handler != null){
            handler.removeCallbacks(runnable);
            handler = null;
        }

        if(onTimerFinishedListener != null)
            onTimerFinishedListener = null;

        try {
            if(tickSound != null)
                tickSound.close();
        } catch (IOException ignore) {}
    }

    public void setLastExecution(boolean lastExecution) {
        if(lastExecution && !silent && tickCount > 0)
            player = MediaPlayer.create(getContext(), R.raw.next_exercise_tick);
    }

    public void restoreTime(){
        this.rest = (int)(endTime - System.currentTimeMillis()) / 1000;

        if(this.rest < 0)
            rest = 0;

        this.restRadius = (int)(360 - (step * (totalRest - rest)));
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }
}