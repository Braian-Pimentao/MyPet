package com.aplicacion.mypet.providers;

import android.content.Context;

import com.aplicacion.mypet.utils.CompressorBitmapImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImageProvider {
    StorageReference storage;

    public ImageProvider() {
        storage = FirebaseStorage.getInstance().getReference();
    }

    public UploadTask save(Context context, File file) {
        byte[] imageByte = CompressorBitmapImage.getImage(context,file.getPath(),500,500);
        StorageReference storageReference = storage.child(new Date()+ ".jpg");
        storage =storageReference;
        return storageReference.putBytes(imageByte);
    }

    public StorageReference getStorage() {
        return storage;
    }

}
