package fr.yncrea.pyjabank.library.steganography.Text.AsyncTaskCallback;

import fr.yncrea.pyjabank.library.steganography.Text.ImageSteganography;

/**
 * This the callback interface for TextEncoding AsyncTask.
 */

public interface TextEncodingCallback {

    void onStartTextEncoding();

    void onCompleteTextEncoding(ImageSteganography result);

}
