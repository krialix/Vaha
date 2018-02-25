package com.vaha.server.listener

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.vaha.server.util.ServerEnv
import java.util.logging.Logger
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

@WebListener
class FirebaseInitializer : ServletContextListener {

    override fun contextInitialized(sce: ServletContextEvent) {
        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .setProjectId("vahaapp-dev")
            .setDatabaseUrl(FIREBASE_URL)
            .build()

        FirebaseApp.initializeApp(options)

        if (ServerEnv.isDev()) {
            val auth = FirebaseAuth.getInstance()

            try {
                val userRecord = auth.getUserByEmailAsync("yasinsinan707@gmail.com").get()
                val userRecord2 = auth.getUserByEmailAsync("demo@demo.com").get()

                auth.deleteUserAsync(userRecord.uid).get()
                auth.deleteUserAsync(userRecord2.uid).get()
            } catch (e: Exception) {
                logger.warning(e.message)
            }
        }
    }

    override fun contextDestroyed(sce: ServletContextEvent) {

    }

    companion object {
        private const val FIREBASE_URL = "https://vahaapp-dev.firebaseio.com"
        private val logger = Logger.getLogger(FirebaseInitializer::class.java.name)
    }
}