package com.jerry.okhttpdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private OkHttpClient client;
    public static final String GET_URL = "http://bz.budejie.com/?typeid=2&ver=3.4.3&no_cry=1&client=android&c=wallPaper&a=wallPaperNew&index=1&size=60&bigid=0";
    public static final String TYPE = "application/octet-stream";
    public static final String POST_URL = "http://zhushou.72g.com/app/gift/gift_list/";
    //    请求条件：platform=2&gifttype=2&compare=60841c5b7c69a1bbb3f06536ed685a48
    public static final String POST_URL2 = "http://admin.wap.china.com/user/NavigateTypeAction.do?processID=getNavigateNews";
    //    请求参数：page=1&code=news&pageSize=20&parentid=0&type=1

    private Button btnGet;
    private Button btnPost;
    private Button btnPostFile;
    private TextView tvShow;

    private void assignViews() {
        btnGet = (Button) findViewById(R.id.btn_get);
        btnPost = (Button) findViewById(R.id.btn_post);
        btnPostFile = (Button) findViewById(R.id.btn_post_file);
        tvShow = (TextView) findViewById(R.id.tv_show);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        initOkHttp();
    }

    private void initOkHttp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public void okhttp(View view) {
        switch (view.getId()) {
            case R.id.btn_get:
                Request request = new Request.Builder()
                        .get()
                        .url(GET_URL)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String string = response.body().string();
//                        Log.i(TAG, "onResponse: "+string);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvShow.setText(string);
                            }
                        });
                    }
                });
                break;
            case R.id.btn_post:
                //    请求条件：platform=2&gifttype=2&compare=60841c5b7c69a1bbb3f06536ed685a48
                //    请求参数：page=1&code=news&pageSize=20&parentid=0&type=1
                RequestBody requestBodyPost = new FormBody.Builder()
                        .add("page", "1")
                        .add("code", "news")
                        .add("pageSize", "20")
                        .add("parentid", "0")
                        .add("type", "1")
                        .build();
                Request requestPost = new Request.Builder()
                        .url(POST_URL)
                        .post(requestBodyPost)
                        .build();
                client.newCall(requestPost).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String string = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvShow.setText(string);
                            }
                        });
                    }
                });
                break;
            case R.id.btn_post_file:
                File file = new File(Environment.getExternalStorageDirectory(), "dd.mp4");
                if (!file.exists()) {
                    Toast.makeText(MainActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
                } else {
                    RequestBody fileBody = RequestBody.create(MediaType.parse(TYPE), file);
                    RequestBody requestBody = new MultipartBody.Builder().addFormDataPart("filename", file.getName(), fileBody).build();

                    Request requestPostFile = new Request.Builder()
                            .url("http://10.11.64.50/upload/UploadServlet")
                            .post(requestBody)
                            .build();
                    client.newCall(requestPostFile).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvShow.setText(response.toString());
                                }
                            });
                        }
                    });
                }
                break;
        }
    }
}
