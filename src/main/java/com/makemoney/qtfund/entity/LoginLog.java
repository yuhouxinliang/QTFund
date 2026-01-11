package com.makemoney.qtfund.entity;

import java.util.Date;

public class LoginLog {
    private String ip;
    private String deviceInfo; // User-Agent
    private Date loginTime;

    public LoginLog() {}

    public LoginLog(String ip, String deviceInfo, Date loginTime) {
        this.ip = ip;
        this.deviceInfo = deviceInfo;
        this.loginTime = loginTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}
