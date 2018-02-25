package com.vaha.server.notification

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.appengine.api.urlfetch.HTTPHeader
import com.google.appengine.api.urlfetch.HTTPMethod
import com.google.appengine.api.urlfetch.HTTPRequest
import com.google.appengine.api.urlfetch.URLFetchService
import com.vaha.server.auth.OAuth2
import com.vaha.server.common.annotations.NoArgs
import com.vaha.server.util.ServerEnv
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.logging.Logger

class NotificationService(private val service: URLFetchService) {

    fun subscribeTopic(fcmToken: String, topicName: String) {
        val url = URL("https://iid.googleapis.com/iid/v1/$fcmToken/rel/topics/$topicName")
        val request = HTTPRequest(url, HTTPMethod.POST)

        request.addHeader(
            HTTPHeader(
                OAuth2.HeaderType.AUTHORIZATION,
                "key=${System.getProperty("fcm.api.key")}"
            )
        )
        request.addHeader(HTTPHeader(OAuth2.HeaderType.CONTENT_TYPE, OAuth2.ContentType.JSON))

        if (!ServerEnv.isTest()) {
            try {
                service.fetchAsync(request)
            } catch (e: IOException) {
                log.info(e.message)
            }
        }
    }

    fun subscribeTopic(fcmTokens: List<String>, topicName: String) {
        val url = URL("https://iid.googleapis.com/iid/v1:batchAdd")
        val request = HTTPRequest(url, HTTPMethod.POST)

        request.addHeader(
            HTTPHeader(
                OAuth2.HeaderType.AUTHORIZATION,
                "key=${System.getProperty("fcm.api.key")}"
            )
        )
        request.addHeader(HTTPHeader(OAuth2.HeaderType.CONTENT_TYPE, OAuth2.ContentType.JSON))

        val subscribeTopicRequest =
            SubscribeTopicRequest(to = "/topics/$topicName", registrationTokens = fcmTokens)

        request.payload = subscribeTopicRequest.toBytes()

        if (!ServerEnv.isTest()) {
            try {
                service.fetchAsync(request)
            } catch (e: IOException) {
                log.info(e.message)
            }
        }
    }

    fun unsubscribeTopic(fcmToken: String, topicName: String) {
        val url = URL("https://iid.googleapis.com/iid/v1/$fcmToken/rel/topics/$topicName")
        val request = HTTPRequest(url, HTTPMethod.DELETE)

        request.addHeader(
            HTTPHeader(
                OAuth2.HeaderType.AUTHORIZATION,
                "key=${System.getProperty("fcm.api.key")}"
            )
        )
        request.addHeader(HTTPHeader(OAuth2.HeaderType.CONTENT_TYPE, OAuth2.ContentType.JSON))

        if (!ServerEnv.isTest()) {
            try {
                service.fetchAsync(request)
            } catch (e: IOException) {
                log.info(e.message)
            }
        }
    }

    fun listSubscribedTopics(fcmToken: String): FcmResponse? {
        val url = URL("https://iid.googleapis.com/iid/v1/$fcmToken?details=true")
        val request = HTTPRequest(url, HTTPMethod.GET)

        request.addHeader(
            HTTPHeader(
                OAuth2.HeaderType.AUTHORIZATION,
                "key=${System.getProperty("fcm.api.key")}"
            )
        )
        request.addHeader(HTTPHeader(OAuth2.HeaderType.CONTENT_TYPE, OAuth2.ContentType.JSON))

        if (!ServerEnv.isTest()) {
            try {
                val httpResponse = service.fetch(request)
                return ObjectMapper().registerModule(KotlinModule())
                    .readValue(httpResponse.content, FcmResponse::class.java)
            } catch (e: IOException) {
                log.info(e.message)
            }
        }

        return null
    }

    fun send(message: PushMessage) {
        if (!ServerEnv.isTest()) {
            try {
                service.fetchAsync(buildRequest(message.toBytes()))
            } catch (e: IOException) {
                log.info(e.message)
            }
        }
    }

    @Throws(MalformedURLException::class)
    private fun buildRequest(bytes: ByteArray): HTTPRequest {
        val url = URL(FCM_SEND_ENDPOINT)
        val request = HTTPRequest(url, HTTPMethod.POST)

        request.addHeader(
            HTTPHeader(
                OAuth2.HeaderType.AUTHORIZATION,
                "key=${System.getProperty("fcm.api.key")}"
            )
        )
        request.addHeader(HTTPHeader(OAuth2.HeaderType.CONTENT_TYPE, OAuth2.ContentType.JSON))
        request.payload = bytes

        return request
    }

    companion object {
        private const val FCM_SEND_ENDPOINT = "https://fcm.googleapis.com/fcm/send"
        private val log = Logger.getLogger(NotificationService::class.java.name)
    }
}

@NoArgs
data class SubscribeTopicRequest(
    val to: String,
    @JsonProperty("registration_tokens") val registrationTokens: List<String>
) {
    @Throws(IOException::class)
    fun toBytes(): ByteArray = ObjectMapper().registerModule(KotlinModule()).writeValueAsBytes(this)

    @Throws(IOException::class)
    fun toJson(): String = ObjectMapper().registerModule(KotlinModule()).writeValueAsString(this)
}
