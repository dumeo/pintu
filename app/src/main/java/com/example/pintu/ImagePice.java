package com.example.pintu;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImagePice {
    int id = 0;
    Bitmap bitmap = null;
    public ImagePice(int id, Bitmap bitmap){
        this.id = id;
        this.bitmap = bitmap;
    }
}
