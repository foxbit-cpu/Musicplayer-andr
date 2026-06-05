package com.example.musicplayerexam.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.musicplayerexam.R;

public class MusicVisualizerView extends View {
    private Visualizer visualizer;
    private byte[] fft;
    private Paint paint;
    private int width, height;

    public MusicVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.accent_purple));
        paint.setStrokeWidth(6f);
        paint.setStyle(Paint.Style.FILL);
    }

    public void linkToMediaPlayer(int audioSessionId) {
        if (visualizer != null) {
            visualizer.release();
        }
        try {
            visualizer = new Visualizer(audioSessionId);
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                    // не используем
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    MusicVisualizerView.this.fft = fft;
                    postInvalidate();
                }
            }, Visualizer.getMaxCaptureRate() / 2, false, true);
            visualizer.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (fft == null || fft.length < 4) return;

        int barCount = Math.min(fft.length / 4, width / 8);
        if (barCount == 0) barCount = 1;
        float barWidth = (float) width / barCount;

        for (int i = 0; i < barCount; i++) {
            int index = i * 2;
            if (index + 1 >= fft.length) break;
            byte real = fft[index];
            byte imag = fft[index + 1];
            float magnitude = (float) Math.hypot(real, imag);
            float scaled = (float) (Math.log(1 + magnitude) * 12);
            float barHeight = Math.min(scaled, height * 0.8f);
            float hue = 260f + (i / (float) barCount) * 100f;
            int color = Color.HSVToColor(new float[]{hue, 0.8f, 1.0f});
            paint.setColor(color);
            canvas.drawRect(i * barWidth, height - barHeight,
                    (i + 1) * barWidth, height, paint);
        }
    }

    public void release() {
        if (visualizer != null) {
            try {
                visualizer.setEnabled(false);
                visualizer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            visualizer = null;
        }
    }
}