package com.ecommerce.dto;

public class CheckoutRequest {
    private Long userId;
    // payment simulation field: "success" true/false
    private boolean paymentSuccess = true;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public boolean isPaymentSuccess() { return paymentSuccess; }
    public void setPaymentSuccess(boolean paymentSuccess) { this.paymentSuccess = paymentSuccess; }
}
