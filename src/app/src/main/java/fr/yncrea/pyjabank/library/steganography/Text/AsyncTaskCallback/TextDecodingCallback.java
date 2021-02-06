package fr.yncrea.pyjabank.library.steganography.Text.AsyncTaskCallback;

import fr.yncrea.pyjabank.library.steganography.Text.ImageSteganography;

/**
 * This the callback interface for TextDecoding AsyncTask.
 */

public interface TextDecodingCallback {

    void onStartTextDecoding();

    void onCompleteTextDecoding(ImageSteganography result);

}
