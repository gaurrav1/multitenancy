package com.multitenacydemo.security;

import jakarta.persistence.Column;

public class RegisterRequest {
	private String dbName;
	private String url;
	private String dbPwd;
    
    public String getDbName() {
        return dbName;
    }
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDbPwd() {
        return dbPwd;
    }
    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }

    
}
