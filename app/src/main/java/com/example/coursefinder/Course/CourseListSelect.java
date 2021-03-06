package com.example.coursefinder.Course;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.coursefinder.MemberVo.MemberLogInResults;
import com.example.coursefinder.R;
import com.example.coursefinder.courseVo.CourseInfo;
import com.example.coursefinder.courseVo.SelectFromView;
import com.example.coursefinder.mycourse.MyCourse;
import com.example.coursefinder.searchVo.ImageSearchResult;
import com.example.coursefinder.searchVo.PlaceList;
import com.example.coursefinder.searchVo.PlaceSearchResult;
import com.example.coursefinder.searchVo.ResultPath;
import com.example.coursefinder.searchapi.ApiClient;
import com.example.coursefinder.searchapi.ApiClient3;
import com.example.coursefinder.searchapi.ApiInterface;
import com.google.gson.Gson;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseListSelect extends AppCompatActivity implements OnMapReadyCallback {
    GridView grid;
    String[] web = {
            "Google",
            "Github",
            "Instagram",
            "Facebook",
            "Flickr",
            "Pinterest",
            "Quora",
            "Twitter",
            "Vimeo",
            "WordPress",
            "Youtube",
            "Stumbleupon",
            "SoundCloud",
            "Reddit",
            "Blogger"

    } ;
    int[] imageId = {
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,
            R.drawable.course_image,

    };


    private static int cnt=0;
    private ArrayList<CourseInfo> coursePlaces = new ArrayList<CourseInfo>();
    // ???????????? ?????? ??????
    private NaverMap navermap;
    // ???????????? ??????
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    // json????????? ????????? ???????????? ??????
    private Map<Integer, ArrayList<PlaceList>> orderschResults = new HashMap<Integer, ArrayList<PlaceList>>();
    private PlaceSearchResult placeSearchResult;
    private ImageSearchResult imageSearchResult;
    private ArrayList<PlaceList> placeList = new ArrayList<PlaceList>();
    private MemberLogInResults loginMember;

    private ResultPath resultPath = new ResultPath();
    private Gson gson;
    private SelectFromView selectFromView;

    private TextView cname;
    private TextView cprice;
    private TextView cinfo;

    private ImageButton fav;
    private SharedPreferences sharedPreferences;

    String[] category_list;
    private String[] course_arr;
    private String imgResults;
    String member;
    String TAG = "TAG";

    private int[] index_arr;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        TextView user = null;
        TextView review = null;

        cname = (TextView) findViewById(R.id.course_name);
        cprice = (TextView) findViewById(R.id.textView8);
        cinfo = (TextView) findViewById(R.id.textView7);

        Button regit_btn = (Button) findViewById(R.id.regit_btn);

        Intent intent = getIntent();
        String[] results = intent.getStringArrayExtra("results");
        String course = intent.getStringExtra("course");
        String[] img_list;
        category_list = intent.getStringArrayExtra("category");
        course_arr = course.split("->");
        String ciid = intent.getIntExtra("courseId", 0)+"";
        int position = intent.getIntExtra("position", 0);
        int size = course_arr.length;
        index_arr = new int[size];
        img_list = new String[size];

        if(size==3) {
            int x = (position / size) / size;
            int y = (position / size) % size;
            int z = position % size;
            index_arr[0] = x;
            index_arr[1] = y;
            index_arr[2] = z;
        }
        else if(size==4){
            int x = ((position / size) /size)/size;
            int y = ((position / size) / size) % size;
            int z = (position / size) % size;
            int k = position % size;

            index_arr[0] = x;
            index_arr[1] = y;
            index_arr[2] = z;
            index_arr[3] = k;
        }
        gson = new Gson();

        try{
            // ?????? ??????, async??? ????????? ????????? ?????? try catch??? ????????? ???????????? ???
            for(int i=0; i<size; i++){
                placeSearchResult = gson.fromJson(results[i], PlaceSearchResult.class);
                orderschResults.put(i+1, placeSearchResult.getPlaceLists());
            }

        }catch(Exception e){
            Log.d(TAG, "?????? ?????? ??????" + e.getMessage());
        }


        try {
            // ????????? ??????, ????????? ????????? ??? ?????? ???????????? ?????? 10??? ????????? ??????
            for (int i = 1; i <= category_list.length; i++) {
                imgResults = new GetImgResult(orderschResults.get(i).get(index_arr[i-1]).getTitle().replaceAll("<b>", "").replaceAll("</b>", "")).execute().get();
                imageSearchResult = gson.fromJson(imgResults, ImageSearchResult.class);
                if (imageSearchResult.getImgResult() == null)
                    orderschResults.get(i).get(0).setImgLink("empty");
                else
                    orderschResults.get(i).get(0).setImgLink(imageSearchResult.getImgResult().get(0).getLink());
            }
        }catch(Exception e){
            Log.d(TAG, "????????? ?????? ??????" + e.getMessage());
        }

        sharedPreferences = getSharedPreferences("Member", MODE_PRIVATE);
        member = sharedPreferences.getString("MemberInfo", "null");
        loginMember = gson.fromJson(member, MemberLogInResults.class);
        member = loginMember.getMemberInfo().get(0).getId();
        // ???????????? ?????? (?????? ??????)
        fav = (ImageButton)findViewById(R.id.add_btn);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ?????? ??????(?????????)??? ????????????.
                sharedPreferences = getSharedPreferences("Member", MODE_PRIVATE);
                String member = sharedPreferences.getString("MemberInfo", "null");
                Gson gson = new Gson();
                MemberLogInResults loginMember = gson.fromJson(member, MemberLogInResults.class);
                member = loginMember.getMemberInfo().get(0).getId();

                if(member != null){
                    saveCourse(ciid, member);
                }
            }
        });

        if(size==3) {
            if(orderschResults.get(1).get(index_arr[0]).getImgLink()!=null) {
                img_list[0] = orderschResults.get(1).get(index_arr[0]).getImgLink();
            }
            else{
                img_list[0] = "no_image";
            }
            if(orderschResults.get(2).get(index_arr[1]).getImgLink()!=null) {
                img_list[1] = orderschResults.get(2).get(index_arr[1]).getImgLink();
            }
            else{
                img_list[1] = "no_image";
            }
            if(orderschResults.get(3).get(index_arr[2]).getImgLink()!=null) {
                img_list[2] = orderschResults.get(3).get(index_arr[2]).getImgLink();
            }
            else{
                img_list[2] = "no_image";
            }
        }
        else if(size==4){
            if(orderschResults.get(1).get(index_arr[0]).getImgLink()!=null) {
                img_list[0] = orderschResults.get(1).get(index_arr[0]).getImgLink();
            }
            else{
                img_list[0] = "no_image";
            }
            if(orderschResults.get(2).get(index_arr[1]).getImgLink()!=null) {
                img_list[1] = orderschResults.get(2).get(index_arr[1]).getImgLink();
            }
            else{
                img_list[1] = "no_image";
            }
            if(orderschResults.get(3).get(index_arr[2]).getImgLink()!=null) {
                img_list[2] = orderschResults.get(3).get(index_arr[2]).getImgLink();
            }
            else{
                img_list[2] = "no_image";
            }
            if(orderschResults.get(4).get(index_arr[3]).getImgLink()!=null) {
                img_list[3] = orderschResults.get(4).get(index_arr[3]).getImgLink();
            }
            else{
                img_list[3] = "no_image";
            }
        }


        for(int i=0; i<size; i++){
            placeList.add(orderschResults.get(i+1).get(index_arr[i]));
        }
        // ????????? ??????
/*????????? ?????? ????????????*/
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
//
//        cname.setText(coursePlaces.get(0).getCi_name());
//        cprice.setText(coursePlaces.get(0).getCi_price()+"");
//        cinfo.setText(coursePlaces.get(0).getCi_info());
        // gridview???????????? ????????????
        /*????????????*/

        CourseListSelectGrid adapter = new CourseListSelectGrid(CourseListSelect.this, imageId, course_arr, category_list, orderschResults, position, index_arr);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        regit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String results = null;
                String name = member+"??? ??????";
                String info = "";
                int price = 0;
                String ci_cata ="";
                // ????????? 3?????? ???????????? cata??? ?????? ???????????? ???????????? '@'??? ????????? ???????????? ??? ????????? ???
                for(int i=0; i<3; i++){
                    ci_cata += placeList.get(i).getCategory()+"@";
                }

                //  makeCourseInfo(cname, cinfo, price, placeLists.get(0).getImgLink(), finalMember, ci_cata);
                try{
                    results = new CourseListSelect.MakeCourseInfo(name, info, price, placeList.get(0).getImgLink(), member, ci_cata).execute().get();
                }catch(Exception e){
                    Log.d("TAG", "INSERT FAILED");
                }

                if(results != null){
                    for(int i=0; i<3; i++){
                        makeCourse(name , placeList.get(i), price, i+1, placeList.get(i).getImgLink());
                    }
                }

            }
        });
//        regit_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String coursename = "example1";
//                String[] courselist = course_arr;
//                String[] imglist = img_list;
//
//                doRegister(coursename, courselist, imglist);
//            }
//        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getBaseContext (), CourseChangePlace.class);
                intent.putExtra("category", category_list[position]);
                intent.putExtra("ordersch_idx", position+1);
                intent.putExtra("index_arr", index_arr[position]);
                launcher.launch(intent);
            }
        });


        //????????? ?????? ????????? ???????????? ???????????? ????????? ????????? ?????????
        /*
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReviewDetail.class); //?????? ????????????, ???????????? ?????? ????????????
                startActivity(intent); //???????????? ??????

            }
        });
        //?????? ?????? ????????? ?????? ?????? ????????? ?????????
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Review.class); //?????? ????????????, ???????????? ?????? ????????????
                startActivity(intent); //???????????? ??????

            }
        });

         */

    }


    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult data)
                {
                    Log.d("TAG", "data : " + data);
                    if (data.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent intent = data.getData();
                        PlaceList place = (PlaceList) intent.getSerializableExtra("place");
                        int ordersch_idx = intent.getIntExtra("ordersch_idx", -1);
                        int item_idx = intent.getIntExtra("index_arr", -1);
                        orderschResults.get(ordersch_idx).remove(item_idx);
                        orderschResults.get(ordersch_idx).add(item_idx, place);
                        CourseListSelectGrid adapter = new CourseListSelectGrid(CourseListSelect.this, imageId, course_arr, category_list, orderschResults, position, index_arr);
                        grid=(GridView)findViewById(R.id.grid);
                        grid.setAdapter(adapter);
                    }
                }
            });


    // ????????? ???????????? ??????
//    @Override
//    public void onMapReady(@NonNull NaverMap naverMap) {
//        this.navermap = naverMap;
        // ???????????? ??????
        // ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        // ????????? ???????????? ????????? ??????????????? ??????, ????????? polyline?????? ??????????????????
        //    getRoute();
        // ?????? 3?????? ??????
//        setMarkers();
//        // ?????? 3?????? ???????????? ????????????
//        setPolyLines();
//        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(
//                new LatLng(coursePlaces.get(0).getCp_lt(), coursePlaces.get(0).getCp_la()));
//        naverMap.moveCamera(cameraUpdate);
//    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.navermap = naverMap;
        // ?????? 3?????? ??????
        setMarkers();
        // ?????? 3?????? ???????????? ????????????
        setPolyLines();
        Tm128 tm = new Tm128(orderschResults.get(1).get(index_arr[0]).getMapx(), orderschResults.get(1).get(index_arr[0]).getMapy());
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(
                new LatLng(tm.toLatLng().latitude, tm.toLatLng().longitude));
        naverMap.moveCamera(cameraUpdate);
    }

    // ?????? ???????????? ????????? ?????? ??????
    public void setMarkers(){
        Marker marker;
        for(int i=0; i<placeList.size(); i++){
            marker = new Marker();
            Tm128 tm = new Tm128(placeList.get(i).getMapx(), placeList.get(i).getMapy());
            marker.setPosition(tm.toLatLng());
            markers.add(marker);
            markers.get(i).setCaptionText(placeList.get(i).getTitle().replaceAll("<b>", " ").replaceAll("</b>", " "));
            markers.get(i).setCaptionTextSize(20);
            markers.get(i).setMap(navermap);
        }
    }

    // ????????? ???????????? ???????????? ????????????
    public void setPolyLines(){
        PathOverlay path = new PathOverlay();
        List<LatLng> latLngList = new ArrayList<LatLng>();
        for(int i=0; i<markers.size(); i++){
            latLngList.add(markers.get(i).getPosition());
        }
        path.setCoords(latLngList);
        path.setMap(navermap);
    }

    public void saveCourse(String ciid, String miid){
        ApiInterface apiInterface = ApiClient3.getInstance().create(ApiInterface.class);
        Call<String> call = apiInterface.saveCourse(ciid, miid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body().equals("1")){
                    Log.d("TAG", "?????? ?????? ??????!");
                    Intent intent = new Intent(CourseListSelect.this, MyCourse.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    // ????????? ?????????????????? ???????????? ?????????. ?????? ????????? ????????? ??????????????? ????????????.
    class GetDetail extends AsyncTask<Void, Void, String> {
        String ciid;

        public GetDetail(String ciid) {
            this.ciid = ciid;
        }

        @Override
        protected void onPostExecute(String s) { super.onPostExecute(s); }

        @Override
        protected String doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient3.getInstance().create(ApiInterface.class);
            Call<String> call = apiInterface.getcoursedetail(ciid);
            try{
                Response<String> response = call.execute();
                return response.body().toString();
            }catch(Exception e){
                Log.d("TAG", "error occured");
            }return null;
        }
    }

    class GetSchResult extends AsyncTask<Void, Void, String> {
        String keyword;
        // ????????? ?????? ??????
        public GetSchResult(String keyword) {
            this.keyword = keyword;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getInstance().create(ApiInterface.class);
            Call<String> call = apiInterface.getSearchResult("l0EZZiIpcB0ffOfRU99J", "hu5ESzjw59", "local.json", keyword,5);
            try{
                Response<String> response = call.execute();
                return response.body().toString();
            }catch(Exception e){
                Log.d(TAG, "error occured");
            }
            return null;
        }
    }

    // ????????? ????????? ?????? ?????????
    class GetImgResult extends AsyncTask<Void, Void, String>{
        String keyword;

        public GetImgResult(String keyword) {
            this.keyword = keyword;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getInstance().create(ApiInterface.class);
            Call<String> call = apiInterface.getImageResult("l0EZZiIpcB0ffOfRU99J", "hu5ESzjw59", "image", keyword,1, "small");
            try{
                Response<String> response = call.execute();
                if(response.isSuccessful()){
                    return response.body().toString();
                }else{
                    Log.d(TAG, "FAIL TO SEARCH IMG" + response.code());
                    return null;
                }
            }catch(Exception e){
                Log.d(TAG, "error occured");
            }
            return null;
        }
    }
    class MakeCourseInfo extends AsyncTask<Void, Void, String> {
        String name;
        String info;
        int cprice;
        String cimg;
        String miid;
        String ci_cata;

        public MakeCourseInfo(String cname, String cinfo, int cprice, String cimg, String miid, String ci_cata) {
            this.name = cname;
            this.info = cinfo;
            this.cprice = cprice;
            this.cimg = cimg;
            this.miid = miid;
            this.ci_cata = ci_cata;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient3.getInstance().create(ApiInterface.class);

            Call<String> call = apiInterface.insertMapCourseInfo(name, info, cprice, cimg, miid,
                ci_cata.substring(0, ci_cata.lastIndexOf("@")-1));
            try{
                Response<String> response = call.execute();
                return response.body().toString();
            }catch(Exception e){
                Log.d("TAG", "error occured");
            }
            return null;
        }
    }

    // DB??? ????????? ????????? ????????? (COURSEPLACE TABLE)
    public void makeCourse(String courseName, PlaceList places, int price, int order ,String imgLink){
        ApiInterface apiInterface = ApiClient3.getInstance().create(ApiInterface.class);
        Tm128 tm = new Tm128(places.getMapx(), places.getMapy());
        LatLng latLng = tm.toLatLng();
        Call<String> call = apiInterface.insertCourse(
                order, places.getTitle().replaceAll("<b>", "").replaceAll("</b>", ""), latLng.longitude, latLng.latitude, places.getAddress(), imgLink, places.getCategory()
        );

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && (response.body().equals("1")) ){
                    Log.d("TAG", "SUCCESS");
                    cnt++;
                    Log.d("TAG", cnt +"in retrofit");
                    if(cnt==3){
                        Log.d("TAG", "?????? ??? ????????? ??????");
                        cnt = 0;
                        Intent intent = new Intent(CourseListSelect.this, MyCourse.class);
                        startActivity(intent);
                    }
                }else{
                    // ?????? ??????
                    Log.d("TAG", response.body());
                    Toast.makeText(getApplicationContext(),"????????? ??????" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TAG", "????????????!");

            }
        });
    }
}
