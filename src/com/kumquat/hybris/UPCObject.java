package com.kumquat.hybris;

import java.util.HashMap;

public class UPCObject {
	private String upc_code;
    private String upc_e;
    private String ean_code;
    
    private String description;
    private String amount;
    private String product_type;
    private String sub_type;
    private String specific_type;
    

    public UPCObject(String upc_code) {
        this.upc_code = upc_code;
        this.upc_e = "-";
        this.ean_code = "-";
        this.description = "-";
        this.amount = "-";
        this.product_type = "-";
        this.sub_type = "-";
        this.specific_type = "-";
    }

    public void addUPCInformation(HashMap<String, String> upc_info) {
        this.upc_code = upc_info.get("upc_code");
        if (upc_info.get("upc_e") != null) {
        	this.upc_e = upc_info.get("upc_e");
        }
        
        if (upc_info.get("ean_code") != null) {
        	this.ean_code = upc_info.get("ean_code");
        }
        
        if (upc_info.get("description") != null) {
        	this.description = upc_info.get("description");
        }
        
        if (upc_info.get("amount") != null) {
        	this.amount = upc_info.get("amount");
        }
        
        return;
    }

    public String getUPCCode() {
    	return this.upc_code;
    }
    
    public String getUPCECode() {
    	return this.upc_e;
    }
    
    public String getEANCode(){
    	return this.ean_code;
    }
    
    public String getDescription() {
    	return this.description;
    }
    
    public String getAmount() {
    	return this.amount;
    }
    
    public String getProductType() {
    	return this.product_type;
    }
    
    public String getSubType() {
    	return this.sub_type;
    }
    
    public String getSpecificType() {
    	return this.specific_type;
    }
    
    public void printUPCInformation() {
        System.out.printf("%s %s %s %s %s", this.upc_code, this.upc_e, this.ean_code, this.description, this.amount); 
    }
}
