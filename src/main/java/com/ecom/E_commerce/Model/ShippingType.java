package com.ecom.E_commerce.Model;

public enum ShippingType {
    NORMAL("Normal Shipping", 5, 50.0),
    FAST("Fast Shipping", 3, 100.0),
    SUPERFAST("Superfast Shipping", 1, 200.0);

    private final String label;
    private final int estimatedDays;
    private final double extraCharge;

    ShippingType(String label, int estimatedDays, double extraCharge) {
        this.label = label;
        this.estimatedDays = estimatedDays;
        this.extraCharge = extraCharge;
    }

    public String getLabel() {
        return label;
    }

    public int getEstimatedDays() {
        return estimatedDays;
    }

    public double getExtraCharge() {
        return extraCharge;
    }
}
