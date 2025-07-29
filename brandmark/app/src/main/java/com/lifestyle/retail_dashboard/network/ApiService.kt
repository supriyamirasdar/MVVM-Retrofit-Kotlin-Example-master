package com.lifestyle.retail_dashboard.network

import com.lifestyle.retail_dashboard.view.attendance.model.*
import com.lifestyle.retail_dashboard.view.brand_ranking.model.BrandRankResponse
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandChartResponse
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceResponse
import com.lifestyle.retail_dashboard.view.homescreen.model.AppVersion
import com.lifestyle.retail_dashboard.view.inventory_managment.model.InventoryManagementResponse
import com.lifestyle.retail_dashboard.view.login.model.ForgotPasswordResponse
import com.lifestyle.retail_dashboard.view.login.model.LoginRequest
import com.lifestyle.retail_dashboard.view.login.model.LoginResponse
import com.lifestyle.retail_dashboard.view.oje.model.OJEListResponse
import com.lifestyle.retail_dashboard.view.oje.model.OJERequest
import com.lifestyle.retail_dashboard.view.oje.model.OJEResponse
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramRequest
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramResponse
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.NewRACRequest
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACRequest
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACResponse
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.UpdateRACRequest
import com.lifestyle.retail_dashboard.view.store_contact.model.GetRacAvailEmpRequest
import com.lifestyle.retail_dashboard.view.store_contact.model.GetRacAvailEmpResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    //Version Test
    @GET("StylusRest/getKioskAppVersion?deviceType=BRND_APP")
    fun checkAppVersion(): Observable<AppVersion?>
    //Login
    @POST("StylusRest/authenticateBrandStaff")
    fun getLoginDetail(@Body request: LoginRequest?): Observable<LoginResponse?>
    //Change Password
    @POST("StylusRest/updBrandStaffPwd")
    fun changePassword(@Body request: LoginRequest?): Observable<LoginResponse?>
    @POST("StylusRest/sendPasswordToUser")
    fun forgotPassword(@Body request: LoginRequest?): Observable<ForgotPasswordResponse?>

    //Brand Performance
    @POST("StylusRest/getProductBrandData")
    fun getBrandPerformance(@Body request: BrandPerformanceRequest?): Observable<BrandPerformanceResponse?>
    @POST("StylusRest/getProductGraphData")
    fun getBrandPerfGraph(@Body request: BrandPerformanceRequest?): Observable<BrandChartResponse?>

    //Attendance
    @POST("StylusRest/getBrandUserDtls")
    fun getBrandUserDetail(@Body request: BrandPerformanceRequest?): Observable<EmployeeListResponse?>
    @POST("StylusRest/getBrandUserAtt")
    fun getAttendanceHistory(@Body request: AttendanceRequest?): Observable<AttendanceResponse?>
    @POST("StylusRest/getBrandUserAttRept")
    fun getAttendanceGraph(@Body request: AttendanceRequest?): Observable<AttendanceGraphResponse?>
    @POST("StylusRest/getAllStoresLocInfoBasedOnOuCode")
    fun getStoreList(@Body request: StoreListRequest?): Observable<StoreListResponse?>

    //Planogram
    @POST("StylusRest/uploadPlanogram")
    fun uploadPlanogram(@Body request: PlanogramRequest?): Observable<PlanogramResponse?>
    @POST("StylusRest/getPlanogramList")
    fun getPlanogramList(@Body request: PlanogramRequest?): Observable<PlanogramResponse?>
    @POST("StylusRest/deletePlanogram")
    fun deletePlanogram(@Body request: PlanogramRequest?): Observable<PlanogramResponse?>


    @POST("StylusRest/updOjeScore")
    fun updOjeScore(@Body request: OJERequest?): Observable<OJEResponse?>
    @GET("StylusRest/getOjeList")
    fun getOJEList(): Observable<OJEListResponse?>


    @POST("StylusRest/racAudit")
    fun createRetailAudit(@Body request: NewRACRequest?): Observable<OJEResponse?>
    @POST("StylusRest/updRacAudit")
    fun updateRACAudit(@Body request: UpdateRACRequest?): Observable<OJEResponse?>
    @POST("StylusRest/getRacList")
    fun getRACList(@Body request: GetRACRequest?): Observable<GetRACResponse?>

    //Brand Rank
    @POST("StylusRest/getBrandRankingData")
    fun getBrandRankList(@Body request: BrandPerformanceRequest?): Observable<BrandRankResponse?>

    //Inventory Management
    @POST("StylusRest/getInventoryData")
    fun getInventoryManagement(@Body request: BrandPerformanceRequest?): Observable<InventoryManagementResponse?>

    // store contact : emp list
    @POST("StylusRest/getRacAvailEmp")
    fun getRacAvailEmpList(@Body request: GetRacAvailEmpRequest?): Observable<GetRacAvailEmpResponse?>

}