package com.campus.trade.dto;

public class ReviewResponseDTO {

    private boolean pass;
    private String reason;
    private String riskLevel;

    public boolean isPass() { return pass; }
    public void setPass(boolean pass) { this.pass = pass; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}
