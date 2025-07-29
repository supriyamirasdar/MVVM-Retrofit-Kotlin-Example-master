/*
 * ********************************************************************************************************************************************************************************************
 * Program Name : BusinessInfoFault.java
 * Description:
 * Modification History ( Reverse Chronological Order)                                                     																					  *
 *--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 *	 DATE                	DEVELOPER     		 			               MODIFICATION HISTORY             								DEFECT NO     				VERSION               *
 *--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*
 * 6-January-2015         TCS-Shivukumar    				                 Initial Version   				 	          											      1.0                 *
 **********************************************************************************************************************************************************************************************
 */
package com.landmarkgroup.smartkiosk.model;

import java.io.Serializable;

public class BusinessInfoFault implements Serializable {

    private static final long serialVersionUID = 575760690358513736L;
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }
}
