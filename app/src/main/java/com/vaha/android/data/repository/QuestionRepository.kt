package com.vaha.android.data.repository

import com.vaha.android.data.api.VahaService
import com.vaha.android.data.entity.Question
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(private val vahaService: VahaService) {

    fun insertQuestion(content: String, categoryId: String): Completable {
        return vahaService.insertQuestion(content, categoryId)
    }

    fun listQuestions(cursor: String?): Single<List<Question>> {
        return vahaService.listQuestions()
    }
}