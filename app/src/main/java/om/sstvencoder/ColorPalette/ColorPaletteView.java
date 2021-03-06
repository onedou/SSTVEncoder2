/*
Copyright 2017 Olga Miller <olga.rgb@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package om.sstvencoder.ColorPalette;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class ColorPaletteView extends View {

    public interface OnColorSelectedListener {
        void onColorChanged(View v, int color);

        void onColorSelected(View v, int color);

        void onCancel(View v);
    }

    private final ArrayList<OnColorSelectedListener> mListeners;
    private final IColorPalette mPalette;

    public ColorPaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mListeners = new ArrayList<>();
        mPalette = new GridColorPalette(GridColorPalette.getStandardColors(),
                getResources().getDisplayMetrics().density);
    }

    public int getColor() {
        return mPalette.getSelectedColor();
    }

    public void setColor(int color) {
        mPalette.selectColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {
        super.onSizeChanged(w, h, old_w, old_h);
        mPalette.updateSize(w, h);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mPalette.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        boolean consumed = false;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                update(e.getX(), e.getY());
                consumed = true;
                break;
            }
            case MotionEvent.ACTION_UP: {
                float x = e.getX();
                float y = e.getY();
                if (getLeft() <= x && x <= getRight() && getTop() <= y && y <= getBottom())
                    colorSelectedCallback();
                else
                    cancelCallback();
                consumed = true;
                break;
            }
        }
        return consumed || super.onTouchEvent(e);
    }

    private void update(float x, float y) {
        if (mPalette.selectColor(x, y)) {
            invalidate();
            colorChangedCallback();
        }
    }

    public void addOnColorSelectedListener(OnColorSelectedListener listener) {
        mListeners.add(listener);
    }

    private void colorChangedCallback() {
        for (OnColorSelectedListener listener : mListeners) {
            listener.onColorChanged(this, mPalette.getSelectedColor());
        }
    }

    private void colorSelectedCallback() {
        for (OnColorSelectedListener listener : mListeners) {
            listener.onColorSelected(this, mPalette.getSelectedColor());
        }
    }

    private void cancelCallback() {
        for (OnColorSelectedListener listener : mListeners) {
            listener.onCancel(this);
        }
    }
}

