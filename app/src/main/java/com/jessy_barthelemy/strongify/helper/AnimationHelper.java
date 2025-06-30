package com.jessy_barthelemy.strongify.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.jessy_barthelemy.strongify.interfaces.Dismissible;

public class AnimationHelper {
    public static void registerCircularRevealAnimation(final Context context, final View view, final RevealAnimationSettings revealSettings, final int startColor, final int endColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    int cx = revealSettings.centerX;
                    int cy = revealSettings.centerY;
                    int width = revealSettings.width;
                    int height = revealSettings.height;
                    int duration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);

                    float finalRadius = (float) Math.sqrt(width * width + height * height);

                    startColorAnimation(view, startColor, endColor, duration);

                    Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius).setDuration(duration);
                    anim.setInterpolator(new FastOutSlowInInterpolator());
                    anim.start();
                }
            });
        }
    }

    private static void startColorAnimation(final View view, final int startColor, final int endColor, int duration) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, endColor);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.setDuration(duration);
        anim.start();
    }

    public static void startCircularExitAnimation(final Context context, final View view, final RevealAnimationSettings revealSettings, final int startColor, final int endColor, final Dismissible.OnDismissedListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = revealSettings.centerX;
            int cy = revealSettings.centerY;
            int width = revealSettings.width;
            int height = revealSettings.height;
            int duration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);

            float initRadius = (float) Math.sqrt(width * width + height * height);
            startColorAnimation(view, startColor, endColor, duration);

            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0);
            anim.setDuration(duration);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onDismissed();
                }
            });
            anim.start();
        } else {
            listener.onDismissed();
        }
    }

    public static ObjectAnimator getScaleDownAnimation(View view){
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setInterpolator(new FastOutSlowInInterpolator());
        scaleDown.setDuration(600);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        return scaleDown;
    }
}