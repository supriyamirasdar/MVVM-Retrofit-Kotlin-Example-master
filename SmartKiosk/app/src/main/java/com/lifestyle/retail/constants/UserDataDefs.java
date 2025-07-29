package com.lifestyle.retail.constants;

public class UserDataDefs {
    public static enum USER_DATA {
        SCANNER_ROTATION("camerarotate"),
        PRINT_DEFAULT("printdefault"),
        BT_ADDRESS("btaddress"),
        BT_BARCODE("btbarcode"),
        BT_WASPRICE("btwasprice"),
        BT_NOWPRICE("btnowprice"),
        BT_DISCOUNT("btdiscount"),
        BT_FRIENDLY_NAME("btfriendlyname"),

        SCAN_MODE("scanMode"),
        SALE_WITHOUT_SALE("saleWithoutSale"),

        PROMO_ENABLED("promo"),
        ASC_ENABLED("asc"),
        WAS_NOW_ENBLED("wasnow"),
        MISSING_BARCODE("missingbarcode"),
        SOH_ENABLED("soh"),
        SHELF_EDGE_ENBLED("shelfedge"),
        DOWNLOAD_APP("downloadapp"),
        STOCK_SEGGERGATION("stockseggegration"),
        ADMIN_ENABLED("admin"),
        CM_ENABLED("cm"),
        FRA_ENABLED("fra"),
        ACA_ENABLED("aca"),
        OSD_ENABLED("osd"),
        MAX_OSD_ENABLED("maxOSD"),
        ACS_SYS_ENABLED("acs_sys"),
        TRANSFERORDER("transfer_order"),
        DASHBOARD_ENABLED("dash_board"),
        SUPER_ADMIN_ENABLE("super_admin"),
        GW_ENABLE("GiftWraapper"),
        SALE_PERFORMANCE("sale_performance_dashboard"),

        VISUAL_MERCHANDISE_ENABLED("visual_merchandising"),
        OJE_SCORE_ENABLED("oje_score"),
        RETAIL_AUDIT_ENABLED("retail_audit_checklist"),
        BRAND_RANKING_ENABLED("brand_ranking"),
        INVENTORY_MANAGEMENT_ENABLED("inventory_management");
        public final String key;

        USER_DATA(String key) {
            this.key = key;
        }
    }
}