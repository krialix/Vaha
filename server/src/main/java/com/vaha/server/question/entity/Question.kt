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
    @Index var status: Status = Status.AVAILABLE,
    @Index var createdAt: DateTime = DateTime.now(UTC),
    var reAsk: Int = 2,
    @Index @Load var answerer: Ref<Account>? = null,
    @Load var category: Ref<Category>,
    var pendingUserRequests: @JvmSuppressWildcards MutableSet<PendingUserRequest> = mutableSetOf(),
    var schemaVersion: Int = 1
) {
    val key: Key<Question>
        get() = Key.create(this)

    val ownerWebsafeId: String
        get() = parent.toWebSafeString()

    val trimmedNotificationContent: String
        get() =
            if (content.length >= 120) "${content.substring(0, 120)}..." else content

    fun isOwner(requesterKey: Key<Account>): Boolean {
        return requesterKey.equivalent(parent)
    }

    @OnLoad
    fun onLoad() {
        pendingUserRequests =
                if (pendingUserRequests == null) mutableSetOf() else pendingUserRequests
    }

    enum class Status {
        AVAILABLE,
        IN_PROGRESS,
        COMPLETED,
        AUTO_CLOSED
    }

    data class PendingUserRequest(
        val userKey: Key<Account>,
        val username: String,
        val rating: Float
    )

    companion object {
        const val FIELD_STATUS = "status"
        const val FIELD_CREATED_AT = "createdAt"
        const val FIELD_ANSWERER = "answerer"

        fun allocateId(userKey: Key<Account>): Long =
            factory().allocateId(userKey, Question::class.java).id
    }
}