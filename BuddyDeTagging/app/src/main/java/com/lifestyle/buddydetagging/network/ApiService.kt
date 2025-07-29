package com.lifestyle.buddydetagging.network


import com.lifestyle.buddydetagging.view.detagging.model.StylusAppVrsnResponse
import com.lifestyle.buddydetagging.view.detagging.model.StylusAppsVrsnCheckRequest
import com.lifestyle.buddydetagging.view.login.dto.*
import com.lifestyle.buddydetagging.view.login.model.ForgotPasswordRequest
import com.lifestyle.buddydetagging.view.login.model.LoginRequestData
import com.lifestyle.buddydetagging.view.login.model.LoginResponse
import com.lifestyle.landmark_covid_care.view.homescreen.model.AppVersion
import com.validateemp.ValidEmployeeRequest
import com.validateemp.ValidEmployeeResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // brandmark api..

    //Version Test
    @GET("StylusRest/getKioskAppVersion?deviceType=BRND_APP")
    fun checkAppVersion(): Observable<AppVersion?>
    //Login


    // detag api..
    @POST("DetagAuth/authenticateUser")
    fun getLoginDetail(@Body request: UserLoginDTO?): Observable<LoginResponseDTO?>




    //Change Password
    @POST("DetagAuth/changePassword")
    fun changePassword(@Body request: UserLoginDTO?): Observable<ResponseDTO?>

    @POST("DetagAuth/forgotPassword")
    fun forgotPassword(@Body request: ForgotPasswordRequest?): Observable<ResponseDTO?>

    // dt 11-08-2021

    @POST("StylusRest/authenticate")
    fun loginWithPhone(@Body loginRequestData: LoginRequestData?): Observable<LoginResponse?>?


    @POST("StylusRest/getStylusMobAppVersion")
    fun checkMobAppVersion(@Body versionCheckRequest: StylusAppsVrsnCheckRequest?): Observable<StylusAppVrsnResponse?>?

    @GET("DetagAuth/getDetggVrsn")
    fun checkMobAppVersion(): Observable<StylusAppVrsnResponse?>?

    // Employee validation API..
    @POST("StylusRest/validateEmployeeWithEmpId")
    fun getValidEmployee(@Body request: ValidEmployeeRequest?): Observable<ValidEmployeeResponse?>

}