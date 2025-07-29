package com.lifestyle.buddydetagging.network

import com.lifestyle.buddydetagging.utils.PreferenceUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthorizationInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val newBuilder = chain.request().newBuilder()
        if(PreferenceUtils.getAuthKey() != null)
            newBuilder.addHeader("Authorization", PreferenceUtils.getAuthKey())
        else
            newBuilder.addHeader("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMDYzNjk0IiwiZXhwIjoxNTU4MjQzODYxfQ.6YP7m7tNVZ-TeDkyFL0M4Sn-oVvw7rWD7sh4i-AykL_dGoXZ5THNx0PAazu7SFmrXy0HnwNX_qTyTQEnaPfmRA")
        val newRequest = newBuilder.build()
        return chain.proceed(newRequest)
    }
}