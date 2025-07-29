package com.lifestyle.buddydetagging.view.login.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class ResponseDTO implements Parcelable {

    private int statusCode;
    private String statusMessage;
    private String statusErrMessage;


    public String getStatusErrMessage() {
        return statusErrMessage;
    }

    public void setStatusErrMessage(String statusErrMessage) {
        this.statusErrMessage = statusErrMessage;
    }


    public ResponseDTO() {

    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public static final Parcelable.Creator<ResponseDTO> CREATOR = new Parcelable.Creator<ResponseDTO>() {
        @Override
        public ResponseDTO createFromParcel(Parcel in) {
            return new ResponseDTO(in);
        }

        @Override
        public ResponseDTO[] newArray(int size) {
            return new ResponseDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(String.valueOf(this.statusCode));
        dest.writeString(this.statusMessage);
        dest.writeString(this.statusErrMessage);
    }

    protected ResponseDTO(Parcel in) {
        this.statusCode = Integer.parseInt(in.readString());
        this.statusMessage = in.readString();
        this.statusErrMessage = in.readString();
    }
}
