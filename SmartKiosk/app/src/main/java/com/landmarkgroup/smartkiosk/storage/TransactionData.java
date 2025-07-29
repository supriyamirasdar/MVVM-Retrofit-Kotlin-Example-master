package com.landmarkgroup.smartkiosk.storage;

public class TransactionData {

    private int _id;
    private int transaction_id;


    private String invoice_date;
    private String trans_status;                    // complete/incomplete
    private String trans_submit_to_server_status;   // true/false

    private String storeId;
    private String ordConfirmUrl;
    private String kioskId;
    private String custMobileNum;
    private String empId;

    private String orderType;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getTrans_status() {
        return trans_status;
    }

    public void setTrans_status(String trans_status) {
        this.trans_status = trans_status;
    }

    public String getTrans_submit_to_server_status() {
        return trans_submit_to_server_status;
    }

    public void setTrans_submit_to_server_status(String trans_submit_to_server_status) {
        this.trans_submit_to_server_status = trans_submit_to_server_status;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getOrdConfirmUrl() {
        return ordConfirmUrl;
    }

    public void setOrdConfirmUrl(String ordConfirmUrl) {
        this.ordConfirmUrl = ordConfirmUrl;
    }

    public String getKioskId() {
        return kioskId;
    }

    public void setKioskId(String kioskId) {
        this.kioskId = kioskId;
    }

    public String getCustMobileNum() {
        return custMobileNum;
    }

    public void setCustMobileNum(String custMobileNum) {
        this.custMobileNum = custMobileNum;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
