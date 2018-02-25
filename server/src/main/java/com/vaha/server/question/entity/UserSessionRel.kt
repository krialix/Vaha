package com.vaha.server.question.entity

import com.googlecode.objectify.Key
import com.googlecode.objectify.annotation.Cache
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Index
import com.vaha.server.common.annotations.NoArgs
import com.vaha.server.user.entity.Account

@Cache
@Entity
@NoArgs
data class UserSessionRel(
    @Id var id: Long? = null,
    @Index var userKey: Key<Account>,
    @Index var questionKey: Key<Question>,
    @Index var sessionStatus: SessionStatus
) {
    val key: Key<UserSessionRel>
        get() = Key.create(this)

    val websafeId: String
        get() = key.toWebSafeString()

    enum class SessionStatus {
        // user has sent a answer request but not responded yet
        PENDING,
        // user is currently in session for a question
        IN_PROGRESS,
        // session is finished
        COMPLETED,
        // session is started with someone else
        DISCARDED
    }
}