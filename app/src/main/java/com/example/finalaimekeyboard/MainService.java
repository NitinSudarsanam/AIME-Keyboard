package com.example.finalaimekeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class MainService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    boolean isCaps;
    boolean aimeOn;
    Keyboard keyboard;
    Keyboard AIMEKeyboard;
    KeyboardView keyboardView;
    @Override
    public View onCreateInputView() {
        keyboard = new Keyboard(this, R.xml.keyboard_lowercase);
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_custom2, null);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        AIMEKeyboard = new Keyboard(this, R.xml.keyboard_aime);

        return keyboardView;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }


    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        InputConnection inputConnection = getCurrentInputConnection();
        playClick(primaryCode);
        if (inputConnection == null)
        {
            return;
        }
        switch(primaryCode)
        {
            case Keyboard.KEYCODE_DELETE:
                inputConnection.deleteSurroundingText(1,0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                keyboardView.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case 500:
                if (aimeOn)
                {
                    keyboardView.setKeyboard(keyboard);
                    keyboardView.setOnKeyboardActionListener(this);
                }
                else if (!aimeOn)
                {
                    keyboardView.setKeyboard(AIMEKeyboard);
                    keyboardView.setOnKeyboardActionListener(this);
                }
                aimeOn = !aimeOn;
                break;
            case 600:
                break;
            case 700:
                break;
            case 800:
                break;
            case 900:
                break;

                default:
                char code = (char)primaryCode;
                if (Character.isLetter(code) && isCaps)
                {
                    code = Character.toUpperCase(code);
                }
                inputConnection.commitText(String.valueOf(code),1);
        }

        //inputConnection.commitText(String.valueOf((char) primaryCode),1);

    }

    private void playClick(int primaryCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}

