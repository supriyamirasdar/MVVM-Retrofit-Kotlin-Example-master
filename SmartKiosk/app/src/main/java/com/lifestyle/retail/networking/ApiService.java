package com.lifestyle.retail.networking;

import com.landmarkgroup.smartkiosk.model.AppVersion;
import com.landmarkgroup.smartkiosk.model.EComTabConfigRequest;
import com.landmarkgroup.smartkiosk.model.EComTabDetailsResponse;
import com.landmarkgroup.smartkiosk.model.EComTabValidateResponse;
import com.landmarkgroup.smartkiosk.model.FetchAllDataRequest;
import com.landmarkgroup.smartkiosk.model.FetchAllDataResponse;
import com.landmarkgroup.smartkiosk.model.PDPOrderConfirmResponse;
import com.landmarkgroup.smartkiosk.model.PDPPageLoadRequest;
import com.landmarkgroup.smartkiosk.model.PDPPageResponse;
import com.landmarkgroup.smartkiosk.model.ValidEmployeeRequest;
import com.landmarkgroup.smartkiosk.model.ValidEmployeeResponse;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemDetailsAtOptionLevelRequest;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemDetailsAtOptionLevelResponse;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemImgUrlResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

//import com.lifestyle.retail.dasboard.settings.model.EComTabConfigRequest;
//import com.lifestyle.retail.dasboard.settings.model.EComTabDetailsResponse;
//import com.lifestyle.retail.dasboard.settings.model.EComTabValidateResponse;
//import com.lifestyle.retail.model.FetchAllDataRequest;
//import com.lifestyle.retail.model.FetchAllDataResponse;


public interface ApiService {

    // @@@@@@@@@  smartKiosk APi
    @POST("StylusRest/getAllStoresLocInfoBasedOnOuCode")
    Observable<FetchAllDataResponse> fetchAllStoreData(@Body FetchAllDataRequest request);

    @POST("StylusRest/getTabList")
    Observable<EComTabDetailsResponse> tabListRequest(@Body EComTabConfigRequest request);

    @POST("StylusRest/validateTab")
    Observable<EComTabValidateResponse> tabValidateRequest(@Body EComTabConfigRequest request);

    @POST("StylusRest/mapOrUpdateEComTab")
    Observable<EComTabValidateResponse> tabUpdateRequestNew(@Body EComTabConfigRequest request);


    @POST("StylusRest/kioskEcomUrl")
    Observable<PDPPageResponse> getPDPPageReq(@Body PDPPageLoadRequest request);
    @POST("StylusRest/saveKioskOrdDlts")
    Observable<PDPOrderConfirmResponse> saveOrderAcceptedUrl(@Body PDPPageLoadRequest request);

    @GET("StylusRest/getKioskAppVersion?deviceType=Personal")
    Observable<AppVersion> getUpdatedAppVersion();


    @POST("StylusRest/getItemDetailsAtOptionLevel")
    Observable<GetItemDetailsAtOptionLevelResponse> getItemDetailsAtOptionLevel(@Body GetItemDetailsAtOptionLevelRequest getDivisonListRequest);

    @POST("StylusRest/getMissingBarcodeDetails")
    Observable<GetItemDetailsAtOptionLevelResponse> getMissingBarcodeDetails(@Body GetItemDetailsAtOptionLevelRequest getDivisonListRequest);

    @POST("StylusRest/getItemDetailsForSOH")
    Observable<GetItemDetailsAtOptionLevelResponse> getItemDetailsForSOH(@Body GetItemDetailsAtOptionLevelRequest getDivisonListRequest);

    @GET("StylusRest/getItemImgUrl")
    Observable<GetItemImgUrlResponse> getItemImgUrl(@Query("itemId") String item_id  );


    @POST("StylusRest/validateEmployeeWithEmpId")
    Observable<ValidEmployeeResponse> validateEmployee(@Body ValidEmployeeRequest validEmployeeRequest);

    //@@@@@@@@@   Stylus api


}