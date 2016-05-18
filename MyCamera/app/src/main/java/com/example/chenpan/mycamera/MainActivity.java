package com.example.chenpan.mycamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private String mFilePath;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView = (ImageView) findViewById(R.id.img);
        mFilePath = Environment.getExternalStorageDirectory().getPath();//获取手机存储路径，根目录
        mFilePath = mFilePath + "/" + "test.png";//创建我们要存储图片的路径,以及文件名


    }

    public void StarCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//指定摄像功能
        Uri uri = Uri.fromFile(new File(mFilePath));//创建我们存储的URI路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//指定输出照片的路径为我们指定的路径，更改照片存储的路径
        startActivityForResult(intent, 1);//利用带返回值的跳转进行接收数据，请求码为1

    }

    /**
     * 点击按钮跳转到自定义相机界面
     * @param view
     */
    public void goCustom(View view) {
        Intent intent = new Intent(this, CustomCameraActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //照片数据在data里面
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//返回成功
            if (requestCode == 1) {//请求码是1
                //获取照片数据
//                Bundle bundle = data.getExtras();//获取封装照片数据的bundle
//                Bitmap bitmap = (Bitmap) bundle.get("data");//获取照片数据
//                imageView.setImageBitmap(bitmap);//显示图片
                FileInputStream fileInputStream = null;//定义一个流对象，读取文件都是通过流
                try {
                    fileInputStream = new FileInputStream(mFilePath);//创建我们读取文件的流，指定读取哪个文件
                    Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);//将流转换成我们需要的图片数据
                    imageView.setImageBitmap(bitmap);//显示图片
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
