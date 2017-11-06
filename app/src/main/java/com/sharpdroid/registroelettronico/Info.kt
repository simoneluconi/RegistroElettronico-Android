package com.sharpdroid.registroelettronico

import java.util.concurrent.atomic.AtomicInteger

object Info {
    val ACCOUNT = "currentProfile"
    val API_URL = "https://web.spaggiari.eu/rest/v1/"

    val notificationId = AtomicInteger(0)
}
