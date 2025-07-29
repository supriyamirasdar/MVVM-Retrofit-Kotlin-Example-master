package com.lifestyle.buddydetagging.storage;

import java.io.Serializable;

public class TransactionData implements Serializable {

    private int _id;
    private int transaction_id;
    private String client_generated_transid;

    private String trans_data;

    private String trans_date;
    private String trans_status;                    // complete/incomplete
    private String trans_submit_to_server_status;   // true/false

    private String customer_code;
    private String customer_name;
    private String customer_contact;
    private String emp_id;

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

    public String getClient_generated_transid() {
        return client_generated_transid;
    }

    public void setClient_generated_transid(String client_generated_transid) {
        this.client_generated_transid = client_generated_transid;
    }

    public String getTrans_data() {
        return trans_data;
    }

    public void setTrans_data(String trans_data) {
        this.trans_data = trans_data;
    }

    public String getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(String trans_date) {
        this.trans_date = trans_date;
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

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_contact() {
        return customer_contact;
    }

    public void setCustomer_contact(String customer_contact) {
        this.customer_contact = customer_contact;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }
}
