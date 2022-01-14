package com.example.pintu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static int[] ImageViewPos = new int[20];//记录每个ImageView的位置
    public static ArrayList<ImagePice> orderedImagePices = new ArrayList<>();
    public  ViewPice BlankViewPice;
    private ImageView selectedImage;
    private TextView timeView;
    public boolean firstInit = true;
    public boolean needSucced = false;
    private Button make_succed;
    public  int duration = 0;
    public boolean useFirstInit = true;
    Button startBt, endBt, selectBt;
    public Bitmap bitmap;
    public boolean success = true;
    private static int RESULT_LOAD_IMAGE = 10;
    private int Timecnt = 0;
    private boolean stop = true; //游戏因用户点击或者选择图片结束
    private boolean isRunning = false; //计时是否正在执行，用来标志startBt

    private ImageView iv1, iv2, iv3, iv4, iv5, iv6, iv7, iv8, iv9, iv10;
    private ArrayList<ImagePice> imagePices = new ArrayList<>();
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private ArrayList<ViewPice> viewPices = new ArrayList<>();
    private Handler handler = new Handler();


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
                if(isRunning) timeView.setText("用时:" + (++Timecnt));
                Log.e("time",  "" + (Timecnt));
                handler.postDelayed(runnable, 1000);
                if(isRunning) startBt.setText("重新开始");
                else startBt.setText("开始");

        }
    };
    Thread timeThread = new Thread(runnable);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sunset);
        initData();
        timeThread.start();
        //getRunningPermission();





        startBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop = false;
                initData();
                isRunning = true;
                arrangeBlankView();

            }
        });

        selectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop = true;
                isRunning = false;
                timeView.setText("");
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

                startActivityForResult(intent, RESULT_LOAD_IMAGE);


            }
        });

        endBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop = true;
                isRunning = false;
                int tmp = Timecnt;
                checkSuccess();
                AlertDialog.Builder endDialog =
                        new AlertDialog.Builder(MainActivity.this);
                if(success) endDialog.setIcon(R.drawable.success);
                else endDialog.setIcon(R.drawable.failed);
                endDialog.setTitle((success ? "恭喜通关！" : "游戏结束!") + "用时" + tmp + "秒" );
                endDialog.setNegativeButton("再来一局",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                initData();
                            }
                        });

                endDialog.setPositiveButton("确定", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                endDialog.show();


            }
        });


    }





    private void initData(){
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);
        iv5 = findViewById(R.id.iv5);
        iv6 = findViewById(R.id.iv6);
        iv7 = findViewById(R.id.iv7);
        iv8 = findViewById(R.id.iv8);
        iv9 = findViewById(R.id.iv9);
        iv10 = findViewById(R.id.iv10);
        selectedImage = findViewById(R.id.selectedImage);
        startBt = findViewById(R.id.start_bt);
        endBt = findViewById(R.id.endGame);
        selectBt = findViewById(R.id.selectButton);
        timeView = findViewById(R.id.timeView);
        make_succed = findViewById(R.id.maske_succed);



        imageViews.add(iv1);
        imageViews.add(iv2);
        imageViews.add(iv3);
        imageViews.add(iv4);
        imageViews.add(iv5);
        imageViews.add(iv6);
        imageViews.add(iv7);
        imageViews.add(iv8);
        imageViews.add(iv9);
        imageViews.add(iv10);

        Timecnt = 0;
        needSucced = false;
        timeView.setText("");
        selectedImage.setImageBitmap(this.bitmap);
        ImageProcessor imageProcessor = new ImageProcessor();
        imagePices = imageProcessor.splitImage(this.bitmap); //分割图片形成ID

        for(int i = 0;i < imagePices.size(); i ++){
            ImagePice imagePice = imagePices.get(i);
            ViewPice viewPice = new ViewPice(imagePice, imageViews.get(i));
            viewPice.whichView = i + 1;
            if(firstInit){
                ImageViewPos[viewPice.whichView] = i + 1;
                useFirstInit = false;
            }

            viewPice.updatePos(viewPice.getImageViewPos());
            viewPices.add(viewPice);
        }



        ImagePice imagePice = new ImagePice(10, null);
        BlankViewPice = new ViewPice(imagePice, iv10);
        BlankViewPice.whichView = 10;
        ImageViewPos[BlankViewPice.whichView] = firstInit ? 10 : BlankViewPice.getImageViewPos();
        BlankViewPice.updatePos(BlankViewPice.getImageViewPos());

        viewPices.add(BlankViewPice);

        if(!useFirstInit) firstInit = false;




//-----------------------------------------------------------------
        for(int i = 0; i < viewPices.size(); i ++){
            ViewPice viewPice = viewPices.get(i);
            viewPice.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(MainActivity.this,
//                            "id = " + viewPice.PiceId + ", position = " + ImageViewPos[viewPice.whichView],
//                            Toast.LENGTH_SHORT).show();

                    if(isRunning) Move(viewPice);
                }
            });
        }

        make_succed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeSucced();
            }
        });



    }















    public void checkSuccess(){
        if(!needSucced)
        for (int i = 0;i < viewPices.size(); i ++){
            ViewPice viewPice = viewPices.get(i);
            ImagePice imagePice = viewPice.imagePice;
            int id = imagePice.id;
            if(viewPice.getImageViewPos() != id) success = false;
            Log.e("id-pos", "id = " + viewPice.PiceId + "\npos = "
                    + viewPice.getImageViewPos() + "\n---------------\n");
        }


    }

    public void makeSucced(){
        boolean[] set = new boolean[20];
        for(int i = 0;i < 20; i ++) set[i] = false;
        for(int i = 0; i < orderedImagePices.size(); i ++){
            ImagePice imagePice = orderedImagePices.get(i);
            for(int j = 0; j < viewPices.size(); j ++){
                ViewPice viewPice = viewPices.get(j);
                if(viewPice.getImageViewPos() == imagePice.id){
                    viewPice.setImagePice(imagePice);
                    viewPice.PiceId = viewPice.getImageViewPos();
                    //set[viewPice.getImageViewPos()] = true;
                    break;
                }
            }


        }

        for(int i = 0;i < viewPices.size(); i ++){
            ViewPice viewPice = viewPices.get(i);
            if(viewPice.getImageViewPos() == 10){
                ImagePice imagePice = new ImagePice(10, null);
                viewPice.setImagePice(imagePice);
                break;
            }
        }
        needSucced = true;
        success = true;
        isRunning = false;
    }



    public void Move(ViewPice viewPice){


        ViewPice blankViewPice = this.BlankViewPice;

        //上
        if(viewPice.getImageViewPos() != 1 && viewPice.getImageViewPos() != 2 &&
           viewPice.getImageViewPos() != 3 && viewPice.getImageViewPos() - 3
           == blankViewPice.getImageViewPos()){

            float beforey1 = viewPice.imageView.getY();
            float beforey2 = blankViewPice.imageView.getY();
            float beforex1 = viewPice.imageView.getX();
            float beforex2 = blankViewPice.imageView.getX();
            Log.e("move", "beforex1 = " + beforex1 + ", beforey1 = " + beforey1
             + ", beforex2 = " + beforex2 + ", beforey2 = " + beforey2);

                Log.e("move", "up");
                float dis = Math.abs(viewPice.imageView.getY() -
                        blankViewPice.imageView.getY());
                TranslateAnimation translateAnimation1 = new TranslateAnimation(
                        beforex1, beforex1,beforey1, beforey1 - dis
                );
                TranslateAnimation translateAnimation2 = new TranslateAnimation(
                        beforex2, beforex2,beforey2, beforey2 + dis
                );
                translateAnimation1.setFillAfter(true);
                translateAnimation2.setFillAfter(true);
                translateAnimation1.setDuration(duration);
                translateAnimation2.setDuration(duration);
                viewPice.imageView.startAnimation(translateAnimation1);
                blankViewPice.imageView.startAnimation(translateAnimation2);

                viewPice.imageView.clearAnimation();
                blankViewPice.imageView.clearAnimation();

                viewPice.imageView.setY(beforey1 - dis);
                blankViewPice.imageView.setY(beforey2 + dis);


                int tmp = blankViewPice.getImageViewPos();
                blankViewPice.updatePos(viewPice.getImageViewPos());
                viewPice.updatePos(tmp);

            float aftery1 = viewPice.imageView.getY();
            float aftery2 = blankViewPice.imageView.getY();
            float afterx1 = viewPice.imageView.getX();
            float afterx2 = blankViewPice.imageView.getX();
            Log.e("move", "afterx1 = " + afterx1 + ", aftery1 = " + aftery1
                    + ", beforex2 = " + afterx2 + ", aftery2 = " + aftery2);
        }

        //下
        else if(viewPice.getImageViewPos() != 7 && viewPice.getImageViewPos() != 8
                && viewPice.getImageViewPos() != 9 && viewPice.getImageViewPos() + 3 == blankViewPice.getImageViewPos()){


            float beforey1 = viewPice.imageView.getY();
            float beforey2 = blankViewPice.imageView.getY();
            float beforex1 = viewPice.imageView.getX();
            float beforex2 = blankViewPice.imageView.getX();
            Log.e("move", "beforex1 = " + beforex1 + ", beforey1 = " + beforey1
                    + ", beforex2 = " + beforex2 + ", beforey2 = " + beforey2);

            Log.e("move", "up");
            float dis = Math.abs(viewPice.imageView.getY() -
                    blankViewPice.imageView.getY());
            TranslateAnimation translateAnimation1 = new TranslateAnimation(
                    beforex1, beforex1,beforey1, beforey1 + dis
            );
            TranslateAnimation translateAnimation2 = new TranslateAnimation(
                    beforex2, beforex2,beforey2, beforey2 - dis
            );
            translateAnimation1.setFillAfter(true);
            translateAnimation2.setFillAfter(true);
            translateAnimation1.setDuration(duration);
            translateAnimation2.setDuration(duration);
            viewPice.imageView.startAnimation(translateAnimation1);
            blankViewPice.imageView.startAnimation(translateAnimation2);

            viewPice.imageView.clearAnimation();
            blankViewPice.imageView.clearAnimation();

            viewPice.imageView.setY(beforey1 + dis);
            blankViewPice.imageView.setY(beforey2 - dis);


            int tmp = blankViewPice.getImageViewPos();
            blankViewPice.updatePos(viewPice.getImageViewPos());
            viewPice.updatePos(tmp);

            float aftery1 = viewPice.imageView.getY();
            float aftery2 = blankViewPice.imageView.getY();
            float afterx1 = viewPice.imageView.getX();
            float afterx2 = blankViewPice.imageView.getX();
            Log.e("move", "afterx1 = " + afterx1 + ", aftery1 = " + aftery1
                    + ", beforex2 = " + afterx2 + ", aftery2 = " + aftery2);
        }


        //左
        else if(viewPice.getImageViewPos() != 1 && viewPice.getImageViewPos() != 4
                && viewPice.getImageViewPos() != 7 && viewPice.getImageViewPos() - 1 == blankViewPice.getImageViewPos()){

            float beforey1 = viewPice.imageView.getY();
            float beforey2 = blankViewPice.imageView.getY();
            float beforex1 = viewPice.imageView.getX();
            float beforex2 = blankViewPice.imageView.getX();
            Log.e("move", "beforex1 = " + beforex1 + ", beforey1 = " + beforey1
                    + ", beforex2 = " + beforex2 + ", beforey2 = " + beforey2);

            Log.e("move", "left");
            float dis = Math.abs(viewPice.imageView.getX() -
                    blankViewPice.imageView.getX());
            TranslateAnimation translateAnimation1 = new TranslateAnimation(
                    beforex1, beforex1 - dis,beforey1, beforey1
            );
            TranslateAnimation translateAnimation2 = new TranslateAnimation(
                    beforex2, beforex2 + dis,beforey2, beforey2
            );
            translateAnimation1.setFillAfter(true);
            translateAnimation2.setFillAfter(true);
            translateAnimation1.setDuration(duration);
            translateAnimation2.setDuration(duration);
            viewPice.imageView.startAnimation(translateAnimation1);
            blankViewPice.imageView.startAnimation(translateAnimation2);

            viewPice.imageView.clearAnimation();
            blankViewPice.imageView.clearAnimation();

            viewPice.imageView.setX(beforex1 - dis);
            blankViewPice.imageView.setX(beforex2 + dis);

            int tmp = blankViewPice.getImageViewPos();
            blankViewPice.updatePos(viewPice.getImageViewPos());
            viewPice.updatePos(tmp);
            float aftery1 = viewPice.imageView.getY();
            float aftery2 = blankViewPice.imageView.getY();
            float afterx1 = viewPice.imageView.getX();
            float afterx2 = blankViewPice.imageView.getX();
            Log.e("move", "afterx1 = " + afterx1 + ", aftery1 = " + aftery1
                    + ", beforex2 = " + afterx2 + ", aftery2 = " + aftery2);
        }





        //右
        else if(viewPice.getImageViewPos() != 3 && viewPice.getImageViewPos() != 6
        && viewPice.getImageViewPos() + 1 == blankViewPice.getImageViewPos()){

            float beforey1 = viewPice.imageView.getY();
            float beforey2 = blankViewPice.imageView.getY();
            float beforex1 = viewPice.imageView.getX();
            float beforex2 = blankViewPice.imageView.getX();
            Log.e("move", "beforex1 = " + beforex1 + ", beforey1 = " + beforey1
                    + "\nbeforex2 = " + beforex2 + ", beforey2 = " + beforey2
             + "\n------------------------------\n");


            float dis = Math.abs(viewPice.imageView.getX() -
                    blankViewPice.imageView.getX());
            Log.e("move", "dis = " + dis);


            TranslateAnimation translateAnimation1 = new TranslateAnimation(
                    beforex1, beforex1 + dis, beforey1, beforey1
            );

            TranslateAnimation translateAnimation2 = new TranslateAnimation(
                    beforex2, beforex2 - dis,beforey2, beforey2
            );
            translateAnimation1.setFillAfter(true);
            translateAnimation2.setFillAfter(true);
            translateAnimation1.setDuration(duration);
            translateAnimation2.setDuration(duration);
            viewPice.imageView.startAnimation(translateAnimation1);
            blankViewPice.imageView.startAnimation(translateAnimation2);

            viewPice.imageView.clearAnimation();
            blankViewPice.imageView.clearAnimation();

            viewPice.imageView.setX(beforex1 + dis);
            blankViewPice.imageView.setX(beforex2 - dis);


            int tmp = blankViewPice.getImageViewPos();
            blankViewPice.updatePos(viewPice.getImageViewPos());
            viewPice.updatePos(tmp);
            float aftery1 = viewPice.imageView.getY();
            float aftery2 = blankViewPice.imageView.getY();
            float afterx1 = viewPice.imageView.getX();
            float afterx2 = blankViewPice.imageView.getX();
            Log.e("move", "afterx1 = " + afterx1 + ", aftery1 = " + aftery1
                    + "\neforex2 = " + afterx2 + ", aftery2 = " + aftery2
                    + "\n------------------------------\n");
        }
        else {
            Log.e("MoveNOReaction", "Not moved");
            Log.e("MoveNOReaction", "viewPicePos = " + viewPice.getImageViewPos()
             + ", blankPicePos = " + blankViewPice.getImageViewPos());
            Log.e("MoveNOReaction", ", staticViewPicePos = " + viewPice.getImageViewPos());
        }

    }

    //随机放置空白框
    public void arrangeBlankView(){
        ViewPice blankViewPice = this.BlankViewPice;
        Random r = new Random();
        int pos = (Math.abs(r.nextInt(100)) + 1) % 9;
        //if(pos != blankViewPice.getImageViewPos())

            for(int i = 0;i < viewPices.size(); i ++)
            {
                ViewPice viewPice = viewPices.get(i);
                if(viewPice.PiceId == 9){
                    Log.e("switch", "id = " + viewPice.PiceId);

                    float beforey1 = viewPice.imageView.getY();
                    float beforey2 = blankViewPice.imageView.getY();
                    float beforex1 = viewPice.imageView.getX();
                    float beforex2 = blankViewPice.imageView.getX();
                    Log.e("move", "beforex1 = " + beforex1 + ", beforey1 = " + beforey1
                            + ", beforex2 = " + beforex2 + ", beforey2 = " + beforey2);

                    Log.e("move", "up");
                    float dis1 = Math.abs(viewPice.imageView.getX() -
                            blankViewPice.imageView.getX());
                    float dis2 = Math.abs(viewPice.imageView.getY() -
                            blankViewPice.imageView.getY());

                    boolean BlankOnTheLeft = viewPice.imageView.getX() -
                            blankViewPice.imageView.getX() > 0 ? true : false;

                    boolean BlankOnBelow = viewPice.imageView.getY() -
                            blankViewPice.imageView.getY() < 0 ? true : false;
                    float moveTox1 = BlankOnTheLeft ? beforex1 - dis1 : beforex1 + dis1;
                    float moveTox2 = BlankOnTheLeft ? beforex2 + dis1 : beforex2 - dis1;
                    float moveToy1 = BlankOnBelow ? beforey1 + dis2 : beforey1 - dis2;
                    float moveToy2 = BlankOnBelow  ? beforey2 - dis2 : beforey2 + dis2;


                    TranslateAnimation translateAnimation1 = new TranslateAnimation(
                            beforex1, moveTox1, beforey1, moveToy1
                    );
                    TranslateAnimation translateAnimation2 = new TranslateAnimation(
                            beforex2, moveTox2,beforey2, moveToy2
                    );
                    translateAnimation1.setFillAfter(true);
                    translateAnimation2.setFillAfter(true);
                    viewPice.imageView.startAnimation(translateAnimation1);
                    blankViewPice.imageView.startAnimation(translateAnimation2);

                    viewPice.imageView.clearAnimation();
                    blankViewPice.imageView.clearAnimation();

                    viewPice.imageView.setX(moveTox1);
                    viewPice.imageView.setY(moveToy1);
                    blankViewPice.imageView.setX(moveTox2);
                    blankViewPice.imageView.setY(moveToy2);


                    int tmp = blankViewPice.getImageViewPos();
                    blankViewPice.updatePos(viewPice.getImageViewPos());
                    viewPice.updatePos(tmp);


                    float aftery1 = viewPice.imageView.getY();
                    float aftery2 = blankViewPice.imageView.getY();
                    float afterx1 = viewPice.imageView.getX();
                    float afterx2 = blankViewPice.imageView.getX();
                    Log.e("arrangeBlank", "afterx1 = " + afterx1 + ", aftery1 = " + aftery1
                            + ", beforex2 = " + afterx2 + ", aftery2 = " + aftery2);

                    break;
                }
        }
    }



    @Override
    protected void onActivityResult(int reCode, int resCode, Intent data) {
        super.onActivityResult(reCode, resCode, data);
        Log.e("Image", "start to select picture");
        Uri selectedImageUri = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        //查询我们需要的数据
        Cursor cursor = getContentResolver().query(selectedImageUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Log.e("Image", picturePath);
        this.bitmap = BitmapFactory.decodeFile(picturePath);
        this.selectedImage.setImageBitmap(bitmap);

        cursor.close();
    }

    public void getRunningPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                //如果同意权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //如果拒绝添加权限
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }




}

