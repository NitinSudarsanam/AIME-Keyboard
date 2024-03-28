package com.example.finalaimekeyboard;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;

import java.io.File;
import java.net.URI;
import java.util.Locale;

public class MainService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    boolean isCaps;
    boolean aimeOn;
    Keyboard keyboard;
    Keyboard AIMEKeyboard;
    KeyboardView keyboardView;
    String currentWord = "";

    boolean deleteBeforeBackspace = false;

    TextServicesManager textServicesManager;

    InputConnection inputConnection = getCurrentInputConnection();
    String suggestedWord = "";

    SpellCheckerSession spellCheckerSession;
    @Override
    public View onCreateInputView() {
        keyboard = new Keyboard(this, R.xml.keyboard_lowercase);
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_custom2, null);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        AIMEKeyboard = new Keyboard(this, R.xml.keyboard_aime);
        textServicesManager = (TextServicesManager) getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        Locale locale = Locale.getDefault(); // Use the default locale
        spellCheckerSession = textServicesManager.newSpellCheckerSession(null,locale, new SpellCheckerSession.SpellCheckerSessionListener() {
            @Override
            public void onGetSuggestions(SuggestionsInfo[] results) {
                if (results != null && results.length > 0) {
                    int suggestionsCount = results[0].getSuggestionsCount();
                    if (suggestionsCount > 0)
                    {
                        suggestedWord = results[0].getSuggestionAt(0);
                        inputConnection.deleteSurroundingText(currentWord.length(),0);
                        inputConnection.commitText(suggestedWord,1);
                        suggestedWord = "";
                    }
                }
                currentWord = "";
                inputConnection.commitText(" ", 1);
            }

            @Override
            public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {

            }
        }, false);

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

        inputConnection = getCurrentInputConnection();
        playClick(primaryCode);
        if (inputConnection == null)
        {
            return;
        }
        switch(primaryCode)
        {
            case Keyboard.KEYCODE_DELETE:
                inputConnection.deleteSurroundingText(1,0);
                if (currentWord.length()>0)
                {
                    currentWord = currentWord.substring(0,currentWord.length()-1);
                }
                deleteBeforeBackspace = true;
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                keyboardView.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case 32:
                if (!deleteBeforeBackspace)
                {
                    spellCheckerSession.getSuggestions(new TextInfo(currentWord), 1);
                }
                else {
                    currentWord = "";
                    inputConnection.commitText(" ", 1);
                }
                deleteBeforeBackspace = false;
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
                getLatestImageBitmap();
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
                currentWord += String.valueOf(code);
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

    private Bitmap getLatestImageBitmap()
    {
        String[] projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.MIME_TYPE
            };
            final Cursor cursor = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

    // Put it in the image view
        Bitmap bm = null;
        if (cursor.moveToFirst()) {
                String imageLocation = cursor.getString(1);
                File imageFile = new File(imageLocation);
                if (imageFile.exists()) {   // TODO: is there a better way to do this?
                    bm = BitmapFactory.decodeFile(imageLocation);
                }
            }
        int num = cursor.getCount();

        return bm;
    }
}

