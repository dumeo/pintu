package com.example.pintu;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImageProcessor {
    public ArrayList<ImagePice> splitImage(Bitmap bitmap){
        ArrayList<ImagePice> res = new ArrayList<ImagePice>();
        MainActivity.orderedImagePices.clear();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int piceWidth = Math.min(width, height) / 3;
        for(int i = 0, id = 1;i < 3; i ++)
            for(int j = 0;j < 3; j ++, id ++) {
                int x = j * piceWidth, y = i * piceWidth;
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, x, y, piceWidth, piceWidth);
                ImagePice imagePice = new ImagePice(id, bitmap1);
                Log.e("splitImage id = ", "" + id);
                res.add(imagePice);
                MainActivity.orderedImagePices.add(imagePice);
            }

        Collections.sort(res, new Comparator<ImagePice>() {
            @Override
            public int compare(ImagePice imagePice, ImagePice t1) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });


        return res;
    }


}
