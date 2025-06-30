package com.jessy_barthelemy.strongify.interfaces;

/*An interface to ease the use of back animation*/
public interface Dismissible {
    interface OnDismissedListener {
        void onDismissed();
    }

    void dismiss(OnDismissedListener listener);

    void hide();
}