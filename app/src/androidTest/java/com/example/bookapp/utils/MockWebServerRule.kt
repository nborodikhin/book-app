package com.example.bookapp.utils

import com.example.bookapp.di.MockWebServerHolder
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.rules.ExternalResource

class MockWebServerRule : ExternalResource() {

    val server = MockWebServer()

    override fun before() {
        server.start()
        MockWebServerHolder.url = server.url("/").toString()
    }

    override fun after() {
        server.shutdown()
    }

    fun enqueueResponse(body: String, code: Int = 200) {
        server.enqueue(
            MockResponse()
                .setResponseCode(code)
                .setBody(body)
        )
    }

    fun enqueueError() {
        server.enqueue(
            MockResponse().apply {
                socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
            }
        )
    }
}
