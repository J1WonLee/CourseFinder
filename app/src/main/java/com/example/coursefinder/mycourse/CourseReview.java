package com.example.coursefinder.mycourse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.coursefinder.MemberVo.MemberLogInResults;
import com.example.coursefinder.R;
import com.example.coursefinder.UploadImg;
import com.example.coursefinder.courseVo.CourseInfo;
import com.example.coursefinder.courseVo.CourseListVo;
import com.example.coursefinder.searchapi.ApiClient3;
import com.example.coursefinder.searchapi.ApiInterface;
import com.google.gson.Gson;

import java.io.File;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseReview extends Activity {
    private SharedPreferences sharedPreferences;
    private MemberLogInResults loginMember;
    public static final int PICK_IMAGE = 100;
    private static String filePath = null;
    private static String miid="";
    String uuid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_review);
        Intent intent = getIntent();
        CourseListVo courseListVo = (CourseListVo) (intent.getSerializableExtra("courseInfo"));


        sharedPreferences = getSharedPreferences("Member", MODE_PRIVATE);
        String member = sharedPreferences.getString("MemberInfo", "null");
        Gson gson = new Gson();
        loginMember = gson.fromJson(member, MemberLogInResults.class);
        miid = loginMember.getMemberInfo().get(0).getId();

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(courseListVo.getCi_name());
        Button cancelButton = (Button) findViewById(R.id.button3);
        Button regitButton = (Button) findViewById(R.id.button1);
        Button imgupload = (Button) findViewById(R.id.img_upload);


        // 이미지 등록
        imgupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });


        // 리뷰 등록
        regitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filePath != null){
                    getParts(courseListVo, miid, filePath);
                }else{
                    saveReview(courseListVo, miid,null, null);
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyCourse.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            filePath = UploadImg.getRealPathFromURI_API19(this, data.getData());
            // Glide.with(this).load(filePath).into(img);
        }
    }


    public void getParts(CourseListVo courseListVo, String miid,String filePath){
        uuid = UUID.randomUUID().toString();
        File file = new File(filePath);
        String filename = "test5" + uuid + filePath.substring(filePath.lastIndexOf("."));

        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), reqFile);
        RequestBody idx = RequestBody.create(MediaType.parse("text/plain"), filename);
        saveReview(courseListVo, miid,body,idx);
    }

    public void saveReview(CourseListVo courseListVo, String miid, @Nullable MultipartBody.Part body, @Nullable RequestBody idx){
        RequestBody ciidx = RequestBody.create(MediaType.parse("text/plain"), courseListVo.getCi_idx()+"");
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"),miid);
        RequestBody content = RequestBody.create(MediaType.parse("text/plain"),"이것은내요입니다\n");
        RequestBody crtitle = RequestBody.create(MediaType.parse("text/plain"),"제목을입력하십시오?");
        RequestBody grade = RequestBody.create(MediaType.parse("text/plain"),4+"");


        ApiInterface apiInterface = ApiClient3.getInstance().create(ApiInterface.class);
        Call<String> call = apiInterface.saveReview(ciidx, id,crtitle, content, grade, body, idx);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body().equals("1")){
                    Log.d("TAG", "리뷰 등록 성공!");
                }else{
                    Log.d("TAG", "리뷰 등록에 실패");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TAG", t.getMessage());
            }
        });
    }

    public void uploadImg( MultipartBody.Part body, RequestBody name, RequestBody idx){
        ApiInterface apiInterface = ApiClient3.getInstance().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.imgupload(body, name, idx);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("TAG", "일단 성공" + response.body());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TAG", "오류 발생");
                t.printStackTrace();
            }
        });
    }
}
