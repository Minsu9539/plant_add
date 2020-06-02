package com.example.plnatsub;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface MyAPI{

    @Multipart
    @POST("/account/")
    Call<AccountItem>  upload(@Part MultipartBody.Part file);

    @POST("/account/")
    Call<AccountItem> post_accounts(@Body AccountItem account);

    @PATCH("/account/{pk}/")
    Call<AccountItem> patch_accounts(@Path("pk") int pk, @Body AccountItem account);

    @DELETE("/account/{pk}/")
    Call<AccountItem> delete_accounts(@Path("pk") int pk);

    @GET("/account/")
    Call<List<AccountItem>> get_accounts();

    @GET("/account/{pk}/")
    Call<List<AccountItem>> get_accounts_pk(@Path("pk") int pk);
}