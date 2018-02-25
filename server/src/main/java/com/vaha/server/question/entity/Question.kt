package com.vaha.server.question.entity

import com.googlecode.objectify.Key
import com.googlecode.objectify.Ref
import com.googlecode.objectify.annotation.*
import com.vaha.server.category.entity.Category
import com.vaha.server.common.annotations.NoArgs
import com.vaha.server.ofy.OfyService.factory
import com.vaha.server.user.entity.Account
import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC

@Cache
@Entity
@NoArgs
data class Question(
    @Parent var parent: Key<Account>,
    @Id var id: Long = allocateId(parent),
    var username: String,
    var content: String,
    @Index var questionStatus: QuestionStatus = QuestionStatus.AVAILABLE,
    @Index var createdAt: DateTime = DateTime.now(UTC),
    var reAsk: Int = 2,
    @Load var answerer: Ref<Account>? = null,
    @Load var category: Ref<Category>,
    var schemaVersion: Int = 1,

    @Ignore var owner: Boolean = false,
    @Ignore var requestSent: Boolean = false
) {
    val key: Key<Question> = Key.create(parent, Question::class.java, id)

    val websafeId: String = key.toWebSafeString()

    val ownerWebsafeId: String = parent.toWebSafeString()

    val answererWebsafeId: String? = answerer?.get()?.websafeId

    val answererUsername: String? = answerer?.get()?.username

    val categoryWebsafeId: String = category.get().websafeId

    val categoryNameEn: String = category.get().displayName

    val categoryNameTr: String = category.get().displayNameTr

    val trimmedNotificationContent: String =
        if (content.length >= 120) "${content.substring(0, 120)}..." else content

    fun isOwner(requesterKey: Key<Account>): Boolean {
        return requesterKey.equivalent(parent)
    }

    enum class QuestionStatus {
        AVAILABLE,
        IN_PROGRESS, // active session
        COMPLETED, // session finished
        AUTO_CLOSED
    }

    companion object {
        const val FIELD_STATUS = "questionStatus"
        const val FIELD_CREATED_AT = "createdAt"

        fun allocateId(userKey: Key<Account>): Long =
            factory().allocateId(userKey, Question::class.java).id
    }
}