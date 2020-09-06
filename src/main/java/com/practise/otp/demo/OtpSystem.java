package com.practise.otp.demo;


import lombok.Data;

@Data

public class OtpSystem {

    private String mobilenumber;
    private String otp;
    private long expiryTime;

}
