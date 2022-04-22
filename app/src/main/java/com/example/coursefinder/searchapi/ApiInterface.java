package com.example.coursefinder.searchapi;

import com.example.coursefinder.searchVo.ResultPath;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    // 키워드 검색 함수, @query가 뒤에 붙는 매개변수들, php파일에도 그대로 맵핑을 해줘야 한다.
    // @GET("URL"), @POST("URL") 정의한 ApiClient객체의 BASE_URL +"URL" 형식이다.
    // Call<T> 는 결과 값을 <T> 타입으로 리턴한다는 것을 의미한다.
    // Apiclient에서 retrofit2 객체를 빌드할 때 gson을 통해서 원하는 객체로 바로 변환하면서 담을 수 있지만 안될 경우 String으로 받아서 변화시켜줄 것

    @GET("search/{type}")
    Call<String> getSearchResult(
            @Header("X-Naver-Client-Id") String id,
            @Header("X-Naver-Client-Secret") String pw,
            @Path("type") String type,
            @Query("query") String query,
            // @Query("start") int num,
            @Query("display") int num
    );

    // 이미지 검색 함수
    @GET("search/{type}")
    Call<String> getImageResult(
            @Header("X-Naver-Client-Id") String id,
            @Header("X-Naver-Client-Secret") String pw,
            @Path("type") String type,
            @Query("query") String query,
            @Query("display") int num,
            @Query("small") String filter
    );

    // 경로찾기 검색 함수
    @GET("v1/driving")
    Call<ResultPath> getRoute(
            @Header("X-NCP-APIGW-API-KEY-ID") String id,
            @Header("X-NCP-APIGW-API-KEY") String pw,
            @Query("start") String start,   // "x, y"
            @Query("goal") String goal      // "x, y"
    );

    /**
     * db 연결용
     * db 중에서 select를 통해서 튜플을 받아 오는경우가 아니라면 json으로 받을 일이 거의 없다.
     */


    // DB연결 용 함수
    @FormUrlEncoded     // post일때는 @Field 어노테이션을 이용하는데, @FromUrlEncoded어노테이션을 명시해줘야함
    @POST("Login.php")
    Call<String> doLogin (
            // call.enqueue의 반환 타입은 String으로 지정, 객체로도 반환 가능
            @Field("id") String id,
            @Field("password") String password       // id와 password를 받아온다.
    );


    @FormUrlEncoded
    @POST("dupchk.php")       // 아이디 중복 확인용
    Call<String> dupChk(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("regitmember.php")       // 회원 가입용
    Call<String> insertMember(
            @Field("id") String id,
            @Field("password") String password,
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("birth") String birth,
            @Field("gender") String gender
    );

    @FormUrlEncoded
    @POST("courseregit.php")
    Call<String> insertCourse(
            @Field("name") String name,
            @Field("info") String info,
            @Field("price") int price,
            @Field("img") String img,
            @Field("order") int order,
            @Field("pname") String pname,
            @Field("la") double la,
            @Field("lt") double lt,
            @Field("addr") String addr,
            @Field("pimg") String pimg
    );

    @GET("query.php")
    Call<String> getSelect(     // select , php에서 $_POST[] 를 $_GET[] 으로 바꿔야함
        @Query("country") String country,
        @Query("name") String name
    );

    @FormUrlEncoded
    @POST("update.php")         // 정보 수정용
    Call<String> updateMember(
        @Field("id") String id,
        @Field("country") String country,
        @Field("name") String name
    );

    @Multipart
    @POST("image.php")
    Call<ResponseBody> postImage(   // 이미지 업로드용 (후기에서 씌일거 같아서 만들어 둠)
                                    @Part MultipartBody.Part image, @Part("name") RequestBody name,
                                    @Part("idx") RequestBody idx
    );

}
