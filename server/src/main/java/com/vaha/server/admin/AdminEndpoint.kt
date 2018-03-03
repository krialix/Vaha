package com.vaha.server.admin

import com.google.api.server.spi.ServiceException
import com.google.api.server.spi.auth.common.User
import com.google.api.server.spi.config.ApiClass
import com.google.api.server.spi.config.ApiMethod
import com.google.api.server.spi.config.ApiMethod.HttpMethod.GET
import com.google.api.server.spi.config.ApiMethod.HttpMethod.POST
import com.google.appengine.api.urlfetch.URLFetchServiceFactory
import com.vaha.server.auth.AdminAuthenticator
import com.vaha.server.base.BaseEndpoint
import com.vaha.server.category.client.CategoryClient
import com.vaha.server.category.entity.Category
import com.vaha.server.category.usecase.InsertCategoryUseCase
import com.vaha.server.category.usecase.RefreshCategoriesUseCase
import com.vaha.server.endpointsvalidator.EndpointsValidator
import com.vaha.server.endpointsvalidator.validator.AuthValidator
import com.vaha.server.notification.NotificationService
import com.vaha.server.ofy.OfyService.ofy
import com.vaha.server.question.entity.Question
import com.vaha.server.user.entity.Account
import javax.inject.Named

@ApiClass(authenticators = [AdminAuthenticator::class])
internal class AdminEndpoint : BaseEndpoint() {

    private val notificationService by lazy(LazyThreadSafetyMode.NONE) {
        NotificationService(URLFetchServiceFactory.getURLFetchService())
    }

    @ApiMethod(name = "admin.categories.insert", path = "admin/categories", httpMethod = POST)
    @Throws(ServiceException::class)
    fun insert(@Named("displayName") displayName: String, user: User?): CategoryClient {

        EndpointsValidator().on(AuthValidator.create(user))

        return InsertCategoryUseCase(displayName).run()
    }

    @ApiMethod(name = "admin.refreshTopics", path = "admin/refreshTopics", httpMethod = GET)
    @Throws(ServiceException::class)
    fun refreshTopics(user: User?) {

        EndpointsValidator().on(AuthValidator.create(user))

        val fcmTokens = ofy().load().type(Account::class.java).map { it.fcmToken }

        ofy()
            .load()
            .type(Category::class.java)
            .map { it.topicName }
            .forEach { notificationService.subscribeTopic(fcmTokens, it) }
    }

    @ApiMethod(name = "admin.refreshCategories", path = "admin/refreshCategories", httpMethod = GET)
    @Throws(ServiceException::class)
    fun refreshCategories(user: User?) {

        EndpointsValidator().on(AuthValidator.create(user))

        RefreshCategoriesUseCase().run()
    }

    @ApiMethod(name = "admin.resetUsers", path = "admin/resetUsers", httpMethod = GET)
    @Throws(ServiceException::class)
    fun resetUsers(user: User?) {

        EndpointsValidator().on(AuthValidator.create(user))

        ofy()
            .load()
            .type(Account::class.java)
            .iterable()
            .map { it.resetToDefault() }
            .let { ofy().save().entities(it) }

        ofy()
            .load()
            .type(Category::class.java)
            .iterable()
            .map { it.resetToDefault() }
            .let { ofy().save().entities(it) }

        ofy().delete().keys(ofy().load().type(Question::class.java).keys())
    }
}