package com.vaha.server.notification

import com.vaha.server.common.annotations.NoArgs

@NoArgs
data class FcmResponse(
    val applicationVersion: String,
    val connectDate: String,
    val attestStatus: String,
    val application: String,
    val scope: String,
    val authorizedEntity: String,
    val rel: Rel?,
    val connectionType: String,
    val appSigner: String,
    val platform: String
)

@NoArgs
data class Rel(val topics: Map<String, Any>)