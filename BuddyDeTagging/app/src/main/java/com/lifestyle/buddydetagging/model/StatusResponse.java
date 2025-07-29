package com.lifestyle.buddydetagging.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StatusResponse implements Parcelable {

    private String statusCode;
    private String statusMessage;
    private String statusErrMessage;
    private BusinessInfoFault businessInfoFault;

    public StatusResponse() {

    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusErrMessage() {
        return statusErrMessage;
    }

    public void setStatusErrMessage(String statusErrMessage) {
        this.statusErrMessage = statusErrMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public BusinessInfoFault getBusinessInfoFault() {
        return businessInfoFault;
    }

    public void setBusinessInfoFault(BusinessInfoFault businessInfoFault) {
        this.businessInfoFault = businessInfoFault;
    }


    public static final Creator<StatusResponse> CREATOR = new Creator<StatusResponse>() {
        @Override
        public StatusResponse createFromParcel(Parcel in) {
            return new StatusResponse(in);
        }

        @Override
        public StatusResponse[] newArray(int size) {
            return new StatusResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.statusCode);
        dest.writeString(this.statusMessage);
        dest.writeString(this.statusErrMessage);
    }

    protected StatusResponse(Parcel in) {
        this.statusCode = in.readString();
        this.statusMessage = in.readString();
        this.statusErrMessage = in.readString();
    }

}
