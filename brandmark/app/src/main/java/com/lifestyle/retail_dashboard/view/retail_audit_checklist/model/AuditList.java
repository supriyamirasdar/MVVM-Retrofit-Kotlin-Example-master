package com.lifestyle.retail_dashboard.view.retail_audit_checklist.model;

public class AuditList {
    private  int id;
    private String textLabel;
    private boolean isChecked;
    private boolean isShowYes;
    private boolean isShowNo;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTextLabel() {
        return textLabel;
    }

    public void setTextLabel(String textLabel) {
        this.textLabel = textLabel;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isShowYes() {
        return isShowYes;
    }

    public void setShowYes(boolean showYes) {
        isShowYes = showYes;
    }

    public boolean isShowNo() {
        return isShowNo;
    }

    public void setShowNo(boolean showNo) {
        isShowNo = showNo;
    }
}
