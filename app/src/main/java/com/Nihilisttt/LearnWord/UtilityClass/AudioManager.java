package com.Nihilisttt.LearnWord.UtilityClass;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;

public class AudioManager {
    private static volatile AudioManager instance;
    private MediaPlayer mediaPlayer;
    private final Context appContext;
    private String currentAudioUrl;
    private String pendingAudioUrl;
    private boolean isPreparing;

    private AudioManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static AudioManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AudioManager.class) {
                if (instance == null) {
                    instance = new AudioManager(context);
                }
            }
        }
        return instance;
    }

    public void playAudio(String audioUrl) {
        if (audioUrl == null) {
            showNoAudioToast();
            stopAndReset();
            return;
        }

        // 记录最新请求
        pendingAudioUrl = audioUrl;

        // 立即中断当前播放/准备
        if (mediaPlayer != null) {
            stopAndReset();
        }

        // 开始处理新请求
        startNewPlayback(audioUrl);
    }

    private void startNewPlayback(String audioUrl) {
        currentAudioUrl = audioUrl;
        isPreparing = true;

        try {
            mediaPlayer = new MediaPlayer();
            setupListeners();

            try (AssetFileDescriptor afd = appContext.getAssets().openFd(audioUrl)) {
                mediaPlayer.setDataSource(
                        afd.getFileDescriptor(),
                        afd.getStartOffset(),
                        afd.getLength()
                );
            }

            mediaPlayer.prepareAsync();

        } catch (IOException | IllegalStateException e) {
            Log.e("AudioManager", "Play failed: " + audioUrl, e);
            showNoAudioToast();
            stopAndReset();
        }
    }

    public void stopAudio() {
        pendingAudioUrl = null;
        stopAndReset();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void setupListeners() {
        final String expectedUrl = currentAudioUrl;

        mediaPlayer.setOnPreparedListener(mp -> {
            // 验证是否为最新请求
            if (expectedUrl.equals(currentAudioUrl)) {
                try {
                    isPreparing = false;
                    mp.start();
                    pendingAudioUrl = null;
                } catch (IllegalStateException e) {
                    Log.e("AudioManager", "Start failed", e);
                }
            } else {
                mp.release();
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            // 播放完成后检查待处理请求
            if (pendingAudioUrl != null) {
                startNewPlayback(pendingAudioUrl);
            } else {
                stopAndReset();
            }
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e("AudioManager", "Error: " + what + "/" + extra);
            stopAndReset();
            return true;
        });
    }

    private void stopAndReset() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {
            Log.w("AudioManager", "Stop failed", e);
        } finally {
            mediaPlayer = null;
            currentAudioUrl = null;
            isPreparing = false;
        }
    }

    public void release() {
        pendingAudioUrl = null;
        stopAndReset();
        instance = null;
    }

    private void showNoAudioToast() {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                Toast.makeText(appContext, "该单词暂无读音", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("AudioManager", "Toast error", e);
            }
        });
    }
}
