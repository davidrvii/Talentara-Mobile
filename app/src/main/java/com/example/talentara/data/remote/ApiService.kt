package com.example.talentara.data.remote

import com.example.talentara.data.model.response.user.LoginResponse
import com.example.talentara.data.model.response.user.RegisterResponse
import com.example.talentara.data.model.response.user.UserBasicResponse
import com.example.talentara.data.model.response.user.UserDeleteResponse
import com.example.talentara.data.model.response.user.UserDetailResponse
import com.example.talentara.data.model.response.user.UserUpdateResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    //USER
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("user_name") username: String,
        @Field("user_email") email: String,
        @Field("user_password") password: String,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("user_email") email: String,
        @Field("user_password") password: String,
    ): LoginResponse

    @GET("user/basic/{id}")
    suspend fun getUserBasic(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
    ): UserBasicResponse

    @GET("user/detail/{id}")
    suspend fun getUserDetail(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
    ): UserDetailResponse

    //Seperate or Create Multiple updateUser based on Function Needs
    @FormUrlEncoded
    @PATCH("user/update/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @Field("user_name") username: String,
        @Field("user_email") email: String,
        @Field("user_password") password: String,
        @Field("github") github: String,
        @Field("linkedin") linkedin: String,
        @Field("is_on_project") isOnProject: Int,
        @Field("talent_access") talentAccess: Int,
        @Field("user_image") userImage: String,
        @Field("fcm_token") fcmToken: String,
    ): UserUpdateResponse

    @DELETE("user/delete/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): UserDeleteResponse
}