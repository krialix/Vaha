package com.vaha.server.base

import com.google.api.server.spi.Constant
import com.google.api.server.spi.config.AnnotationBoolean
import com.google.api.server.spi.config.Api
import com.google.api.server.spi.config.ApiNamespace
import com.vaha.server.auth.FirebaseAuthenticator
import com.vaha.server.config.Config

@Api(
    name = "vahaApi",
    version = "v1",
    authenticators = [FirebaseAuthenticator::class],
    namespace = ApiNamespace(
        ownerDomain = Config.API_OWNER,
        ownerName = Config.API_OWNER,
        packagePath = ""
    ),
    clientIds = [Constant.API_EXPLORER_CLIENT_ID, Config.ANDROID_CLIENT_ID, Config.IOS_CLIENT_ID, Config.WEB_CLIENT_ID],
    audiences = [Config.AUDIENCE_ID],
    isAbstract = AnnotationBoolean.TRUE
)
internal abstract class BaseEndpoint