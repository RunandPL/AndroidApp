package com.mp.runand.app.logic.training;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Sebastian on 2014-11-08.
 */
public class MessagesReader implements TextToSpeech.OnInitListener, Runnable {
    private MessagesList messages;
    private TextToSpeech textToSpeech;
    private boolean isReadyToSpeak;
    private boolean running;

    public MessagesReader(Context context) {
        super();
        messages = MessagesList.getInstance();
        textToSpeech = new TextToSpeech(context, this);
        isReadyToSpeak = false;
        running = true;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = new Locale("pl", "PL");
            int result = textToSpeech.isLanguageAvailable(locale);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported");
            } else {
                isReadyToSpeak = true;
            }
        } else {
            Log.e("TTS", "Initialization Failed");
        }
    }

    @Override
    public void run() {
        while(running) {
            //Sleep only to debug
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(isReadyToSpeak && !messages.isEmpty()) {
                String message = messages.getMessage();
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    public void terminate() {
        running = false;
        textToSpeech.shutdown();
        messages.clear();
    }
}
