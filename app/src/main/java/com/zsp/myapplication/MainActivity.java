package com.zsp.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/11/15 0015.
 */

public class MainActivity extends Activity {

    private ImageView imageView;

    private Bitmap bitmap;

    private TextView textView;

    private String iPath = "http://img03.tooopen.com/uploadfile/downs/images/20110714/sy_20110714135215645030.jpg";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        imageView = findViewById(R.id.image);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(t).start();
                //订阅建立关系
//                observable.subscribe(subscriber);
                loadImage();
            }
        });

    }

    private void loadImage() {
        //创建被观察者，作为事件的起点
        Observable
                .just("http://img1.imgtn.bdimg.com/it/u=4127978443,3621625360&fm=11&gp=0.jpg")
                //类型变换--对事件加工变换，这里就是在根据URL地址转换为bitmap
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        return getBitmap(s);
                    }
                })
                //指定被观察者执行的线程环境-io线程
                .subscribeOn(AndroidSchedulers.mainThread())
                //下面程序执行的线程环境-UI线程，要在页面上面显示必须在UI线程执行
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅
                .subscribe(
                        //创建观察者，作为事件的终点，也就是把网络图片显示在imageview上面
                        new Observer<Bitmap>() {
                            @Override
                            public void onCompleted() {
                                // TODO: 2018/11/15 0015  观察结束
                            }

                            @Override
                            public void onError(Throwable e) {
                                // TODO: 2018/11/15 0015   一些报错信息
                            }

                            @Override
                            public void onNext(Bitmap bitmap) {
                                // TODO: 2018/11/15 0015  处理事件
                                imageView.setImageBitmap(bitmap);

                            }
                        });
//                .subscribe(new Action1<Bitmap>() {
//                    @Override
//                    public void call(Bitmap bitmap) {
//                        imageView.setImageBitmap(bitmap);
//
//                    }
//                });
    }

    //为了下载图片资源，开辟一个新的子线程
    Thread t = new Thread() {
        public void run() {
            //下载图片的路径
            try {
                //对资源链接
                URL url = new URL(iPath);
                //打开输入流
                InputStream inputStream = url.openStream();
                //对网上资源进行下载转换位图图片
                bitmap = BitmapFactory.decodeStream(inputStream);
                handler.sendEmptyMessage(111);
                inputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
    };


    /**
     * 下面是RxJava
     */

    //创建被观察者
    Observable<String> observable = Observable.just("http://pic1.nipic.com/2009-01-12/2009112172953294_2.jpg");


    //创建观察者
    Subscriber<String> subscriber = new Subscriber<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String o) {
            Toast.makeText(MainActivity.this, o, Toast.LENGTH_SHORT).show();
        }
    };

    private Bitmap getBitmap(String path) {
        //下载图片的路径
        try {
            //对资源链接
            URL url = new URL(path);
            //打开输入流
            InputStream inputStream = url.openStream();
            //对网上资源进行下载转换位图图片
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
