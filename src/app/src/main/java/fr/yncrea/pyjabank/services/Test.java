package fr.yncrea.pyjabank.services;

import android.content.Context;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class Test {
    public static String storeData(String name, Context context, String data) {
        try {
            MasterKey mainKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    context,
                    new File("/", name + ".txt"),
                    mainKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
                    .build();
            OutputStream outputStream = encryptedFile.openFileOutput();
            byte[] fileContent = data.getBytes(StandardCharsets.UTF_8);
            outputStream.write(fileContent);
            outputStream.flush();
            outputStream.close();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return (name + "txt");
    }

    public static String storeData(String fileName, Context context) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        String s = null;
        try {
            MasterKey mainKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    context,
                    new File("/", fileName),
                    mainKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
                    .build();
            InputStream inputStream = encryptedFile.openFileInput();
            byteArrayOutputStream = new ByteArrayOutputStream();
            int nextByte = inputStream.read();
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte);
                nextByte = inputStream.read();
            }
            s = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IOException generalSecurityException) {
            generalSecurityException.printStackTrace();
        }
        return s;
    }
}
