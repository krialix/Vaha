package com.vaha.server.util

import com.google.common.collect.ImmutableList
import com.googlecode.objectify.Ref
import com.vaha.server.category.entity.Category
import com.vaha.server.question.entity.Question
import com.vaha.server.user.entity.Account
import com.vaha.server.util.TestObjectifyService.fact
import com.vaha.server.util.TestObjectifyService.ofy

object DatastoreHelper {

  /** Persists a single Objectify resource, without adjusting foreign resources or keys.  */
  @JvmStatic
  fun <R> persistSimpleResource(resource: R): R =
      persistSimpleResources(ImmutableList.of(resource))[0]

  /**
   * Like persistResource but for multiple entities, with no helper for saving
   * ForeignKeyedEppResources.
   */
  @JvmStatic
  fun <R> persistSimpleResources(resources: Iterable<R>): ImmutableList<R> {
    ofy().transact { ofy().save().entities(resources) }
    // Force the session to be cleared so that when we read it back, we read from Datastore
    // and not from the transaction's session cache.
    ofy().clear()
    return ImmutableList.copyOf(ofy().load().entities(resources).values)
  }

  @JvmStatic
  fun newAccount(): Account {
    val id = fact().allocateId(Account::class.java).id
    val account = Account(
        id = id,
        username = "test$id",
        email = "test$id@test.com",
        fcmToken = "")

    return persistSimpleResource(account)
  }

  @JvmStatic
  fun newCategory(name: String): Category =
      persistSimpleResource(Category(displayName = name, image = "image_$name", displayNameTr = name))

  @JvmStatic
  fun newQuestion(account: Account, category: Category): Question {
    val question = Question(
        parent = account.key,
        username = account.username,
        content = "testContent",
        category = Ref.create(category))

    return persistSimpleResource(question)
  }
}
