package com.vaha.android.data.api

import com.vaha.android.data.entity.Category
import com.vaha.android.data.entity.Question
import com.vaha.android.data.entity.QuestionResponse
import com.vaha.android.data.entity.User
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface VahaService {

    @GET("me")
    fun getMe(): Single<User>

    @POST("me")
    @FormUrlEncoded
    fun registerUser(@Field("payload") payload: String): Single<User>

    @POST("me/updateFcmToken")
    @FormUrlEncoded
    fun updateFcmToken(@Field("token") token: String): Completable

    @GET("questions")
    fun listQuestions(
        @Query("cursor") cursor: String?,
        @Query("sort") sort: String
    ): Single<QuestionResponse>

    @POST("questions")
    @FormUrlEncoded
    fun insertQuestion(
        @Field("content") content: String,
        @Field("categoryId") categoryId: Long
    ): Single<Question>

    @GET("categories")
    fun listCategories(): Single<List<Category>>

    @POST("sessions/start")
    @FormUrlEncoded
    fun startSession(
        @Field("questionId") questionId: String,
        @Field("answererId") answererId: String
    ): Completable

    @POST("sessions/end")
    fun endSession(
        @Field("questionId") questionId: String,
        @Field("reasked") reasked: Boolean,
        @Field("rating") rating: Int
    ): Completable

    @POST("sessions/request")
    @FormUrlEncoded
    fun sendRequest(@Field("questionId") questionId: String): Completable
}