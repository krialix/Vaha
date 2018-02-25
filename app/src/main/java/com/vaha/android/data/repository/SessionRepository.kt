package com.vaha.android.data.repository

import com.androidhuman.rxfirebase2.database.ChildAddEvent
import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase
import com.google.firebase.database.FirebaseDatabase
import com.vaha.android.data.api.SessionService
import com.vaha.android.data.api.VahaService
import com.vaha.android.data.entity.Question
import com.vaha.android.data.entity.chat.LastUserMessage
import com.vaha.android.data.entity.chat.Message
import com.vaha.server.vahaApi.model.CollectionResponseSessionClient
import com.vaha.server.vahaApi.model.QuestionClient
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val vahaService: VahaService,
    private val sessionService: SessionService
) {

    private val database = FirebaseDatabase.getInstance()

    fun insertQuestion(content: String, categoryId: String): Completable {
        return vahaService.insertQuestion(content, categoryId)
    }

    fun listQuestions(cursor: String?): Single<List<Question>> {
        return vahaService.listQuestions()
    }

    fun startSession(questionId: String, answererId: String): Completable =
        sessionService.startSession(questionId, answererId)

    fun endSession(questionId: String, reasked: Boolean, rating: Int): Completable =
        sessionService.endSession(questionId, reasked, rating)

    fun sendMessage(message: Message, answererId: String, sessionId: String) {
        val messageRef = database.getReference("messages/$sessionId/").push()

        val lastUserMessage = LastUserMessage(
            senderId = message.userId,
            senderUsername = message.username,
            questionId = sessionId,
            lastMessage = message.message,
            timestamp = message.timestamp
        )

        val update = mapOf(
            "messages/$sessionId/${messageRef.key}" to message,
            "users/$answererId" to lastUserMessage
        )

        database.reference.updateChildren(update)
    }

    fun observeUserMessages(userId: String): Observable<LastUserMessage> {
        val userRef = database.getReference("users/$userId/")

        return RxFirebaseDatabase.dataChanges(userRef)
            .map {
                val value = it.getValue(LastUserMessage::class.java)
                return@map value ?: LastUserMessage.EMPTY_LAST_MESSAGE
            }

    }

    fun observeSessionMessages(questionId: String): Observable<Message> {
        val messagesRef = database.getReference("messages/$questionId/")

        return RxFirebaseDatabase.childEvents(messagesRef)
            .ofType(ChildAddEvent::class.java)
            .map {
                val snapshot = it.dataSnapshot()
                snapshot.getValue(Message::class.java)?.copy(id = snapshot.key)
            }
    }

    fun sendAnswererAvailable(questionId: String): Completable =
        sessionService.sendAnswererReady(questionId)

    fun getSessions(status: String): Single<CollectionResponseSessionClient> =
        sessionService.getSessions(status)

    fun checkActiveSession(): Single<QuestionClient> = sessionService.checkActiveSession()
}