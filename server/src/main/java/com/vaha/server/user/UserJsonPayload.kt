package com.vaha.server.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.BaseEncoding
import com.vaha.server.common.annotations.NoArgs
import java.nio.charset.Charset

@NoArgs
internal data class UserJsonPayload(
    val username: String,
    val email: String,
    val password: String,
    val fcmToken: String) {

  companion object {
    fun from(base64Payload: String): UserJsonPayload {
      val decodedBytes = BaseEncoding.base64Url().decode(base64Payload)
      val payload = String(decodedBytes, Charset.defaultCharset())

      val mapper = ObjectMapper()
      return mapper.readValue(payload, UserJsonPayload::class.java)
    }
  }
}
