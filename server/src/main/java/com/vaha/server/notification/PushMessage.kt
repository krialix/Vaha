package com.vaha.server.notification

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.vaha.server.common.annotations.NoArgs

import org.codehaus.jackson.annotate.JsonProperty

import java.io.IOException

@NoArgs
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PushMessage(
    /**
     * This parameter specifies the recipient of a message. The value must be a registration token,
     * notification key, or topic. Do not set this field when sending to multiple topics. See
     * condition.
     */
    val to: String? = null,

    /**
     * This parameter specifies a listFeed of devices (registration tokens, or IDs) receiving a
     * multicast message. It must contain at least 1 and at most 1000 registration tokens. Use this
     * parameter only for multicast messaging, not for single recipients. Multicast messages (sending
     * to more than 1 registration tokens) are allowed using HTTP JSON format only.
     */
    @JsonProperty("registration_ids")
    val registrationIds: List<String>? = null,

    /**
     * This parameter specifies a logical expression of conditions that determine the message target.
     * Supported condition: Topic, formatted as "'yourTopic' in topics". This value is
     * case-insensitive. Supported operators: &&, ||. Maximum two operators per topic message
     * supported.
     */
    val condition: String? = null,

    /**
     * This parameter identifies a group of messages (e.g., with collapse_key: "Updates Available")
     * that can be collapsed, so that only the last message gets sent when delivery can be resumed.
     * This is intended to avoid sending too many of the same messages when the device comes back
     * online or becomes active. Note that there is no guarantee of the order in which messages
     * getPost sent.
     *
     *
     * Note: A maximum of 4 different collapse keys is allowed at any given time. This means a FCM
     * connection server can simultaneously store 4 different send-to-sync messages per client app. If
     * you exceed this number, there is no guarantee which 4 collapse keys the FCM connection server
     * will keep.
     */
    @JsonProperty("collapse_key")
    val collapseKey: String? = null,

    /**
     * Sets the priority of the message. Valid values are "normal" and "high." On iOS, these
     * correspond to APNs priorities 5 and 10.
     *
     *
     * By default, messages are sent with normal priority. Normal priority optimizes the client
     * app's battery consumption and should be used unless immediate delivery is required. For
     * messages with normal priority, the app may receive the message with unspecified delay.
     *
     *
     * When a message is sent with high priority, it is sent immediately, and the app can wake a
     * sleeping device and open a network connection to your server.
     *
     *
     * For more information,
     *
     * @see [](https://firebase.google.com/docs/cloud-messaging/concept-options.setting-the-priority-of-a-message)}.
     */
    val priority: PRIORITY? = null,

    /**
     * This parameter specifies how long (in seconds) the message should be kept in FCM storage if the
     * device is offline. The maximum time to live supported is 4 weeks, and the default value is 4
     * weeks.
     *
     *
     * For more information, see Setting the lifespan of a message.
     */
    @JsonProperty("time_to_live")
    val timeToLive: Int? = null,

    /**
     * This parameter, when set to true, allows developers to test a request without actually sending
     * a message.
     *
     *
     * The default value is false.
     */
    @JsonProperty("dry_run")
    val dryRun: Boolean? = null,

    val notification: NotificationBody? = null,

    val data: Map<String, String>? = null
) {

  @Throws(IOException::class)
  fun toBytes(): ByteArray = MAPPER.writeValueAsBytes(this)

  enum class PRIORITY(priority: String) {
    NORMAL("normal"),
    HIGH("normal")
  }

  data class NotificationBody(
      val body: String? = null,
      val title: String? = null,
      val icon: String? = null)

  companion object {
    private val MAPPER = ObjectMapper().registerModule(KotlinModule())
  }
}
