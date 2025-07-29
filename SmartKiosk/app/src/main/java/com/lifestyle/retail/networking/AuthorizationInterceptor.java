package com.lifestyle.retail.networking;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder newBuilder = chain.request().newBuilder();

      //  MobileAuthResponse mobileAuthResponse = StylusApp.instance.getFromPreference(MobileAuthResponse.class);

       /* MobileAuthResponse mobileAuthResponse = PreferenceUtils.getMobileAuthResponse();
        if (CommonUtility.isNotNull(mobileAuthResponse) && !TextUtils.isEmpty(mobileAuthResponse.getAuthkey())) {

            newBuilder.addHeader("Authorization", "Bearer " + mobileAuthResponse.getAuthkey());

            //newBuilder.addHeader("Authorization", "Bearer " + "K2dIwr0fcre-Qru04nghynM59i-h4RdT");


            Log.e("AuthorizationHeader", mobileAuthResponse.getAuthkey());
            //newBuilder.addHeader("Authorization", "Bearer " + "U5qjk-_tcReprYTxPeLhIQW2PVZUA9LX");

        }*/
        newBuilder.addHeader("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMDYzNjk0IiwiZXhwIjoxNTU4MjQzODYxfQ.6YP7m7tNVZ-TeDkyFL0M4Sn-oVvw7rWD7sh4i-AykL_dGoXZ5THNx0PAazu7SFmrXy0HnwNX_qTyTQEnaPfmRA");
        Request newRequest = newBuilder.build();
        return chain.proceed(newRequest);
    }

}