package com.vaha.android.data.api

import com.vaha.android.data.entity.Category
import com.vaha.android.data.entity.Question
import com.vaha.android.data.entity.QuestionResponse
import com.vaha.android.data.entity.User
import com.vaha.server.vahaApi.model.TopicResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST

interface VahaService {

    @GET("me")
    fun getMe(): Single<User>

    @POST("me")
    fun registerUser(payload: String): Single<User>

    @POST("me/updateFcmToken")
    fun updateFcmToken(token: String): Completable

    @GET("me/listFcmTopics")
    fun listFcmTopics(): Single<List<TopicResponse>>

    @GET("questions")
    fun listQuestions(cursor: String?, sort: String): Single<QuestionResponse>

    @POST("questions")
    fun insertQuestion(content: String, categoryId: String): Single<Question>

    @GET("categories")
    fun listCategories(): Single<List<Category>>

    @POST("sessions/start")
    fun startSession(questionId: String, answererId: String): Completable

    @POST("sessions/end")
    fun endSession(questionId: String, reasked: Boolean, rating: Int): Completable

    @POST("sessions/request")
    fun sendRequest(questionId: String): Completable
}