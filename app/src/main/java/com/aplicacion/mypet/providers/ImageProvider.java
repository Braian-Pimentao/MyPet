package com.aplicacion.mypet.providers;

import android.content.Context;
import android.net.Uri;

import com.aplicacion.mypet.utils.CompressorBitmapImage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ImageProvider {
    private StorageReference storage;

    public ImageProvider() {
        storage = FirebaseStorage.getInstance().getReference();
    }

    public UploadTask save(Context context, File file, int numero) {
        byte[] imageByte = CompressorBitmapImage.getImage(context,file.getPath(),500,500);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(new Date() + "-"+ numero + ".jpg");
        storage = storageReference;
        return storageReference.putBytes(imageByte);
    }

    public Task<Void> deleteByUrl(String urlImagen){
        StorageReference desertRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlImagen);
        return desertRef.delete();
    }

    public Task<Uri> getStorage() {
        return storage.getDownloadUrl();
    }

    public Task<byte[]> getImageFile(String urlImagen) throws IOException {
        StorageReference desertRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlImagen);
        return desertRef.getBytes(1024*1024);
    }

}
