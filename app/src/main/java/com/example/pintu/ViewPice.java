package com.example.pintu;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class ViewPice {
    int PiceId; //小分块的id，等于imagePice的id
    public int whichView = 0; //哪号的ImageView
    public ImageView imageView;
    public ImagePice imagePice;

    public ViewPice(ImagePice imagePice, ImageView imageView){
        this.PiceId = imagePice.id;
        this.imagePice = imagePice;
        this.imageView = imageView;
        imageView.setImageBitmap(imagePice.bitmap);
    }

    public int getImageViewPos() {

         return MainActivity.ImageViewPos[whichView];
    }


    public void updatePos(int pos){
        MainActivity.ImageViewPos[whichView] = pos;
    }

    public void setImagePice(ImagePice imagePice) {
        this.imagePice = imagePice;
        this.PiceId = imagePice.id;
        imageView.setImageBitmap(imagePice.bitmap);
    }
}
