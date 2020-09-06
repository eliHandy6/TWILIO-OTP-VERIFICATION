package com.practise.otp.demo.controller;

import com.practise.otp.demo.OtpSystem;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile_numbers/")
public class OtpSystemRestController {

    private Map<String, OtpSystem> otpdata=new HashMap<>();
    //Twilio authentication variables

    private final static String ACCOUNT_ID="AC1db0da1eeec76702e3841e1225917b1e";
    private final static  String AUTH_ID="393ca745542797c359cc0e47ceb3f6fb";



    static {

        Twilio.init(ACCOUNT_ID,AUTH_ID);
    }


    @PostMapping("/{mobile_number}/obtainOtp")
    public ResponseEntity<Object> sendOTP(@PathVariable("mobile_number")String mobile_number){

        OtpSystem otpSystem=new OtpSystem();

        otpSystem.setMobilenumber(mobile_number);
        otpSystem.setOtp(String.valueOf(((int)(Math.random()*(10000-1000)))+1000)); //maximum is 10000 minimum is 1000
        otpSystem.setExpiryTime(System.currentTimeMillis()+20000);//setting the expiry time to 20 seconds


        otpdata.put(mobile_number,otpSystem);//passing the  values  to the map

        //Creating the message to the phone number

        // to from message

        Message.creator(new PhoneNumber("+254703907872"),new PhoneNumber("+13396138262"),"your OTP is :"+otpSystem.getOtp()).create();

        return new ResponseEntity<>("Your OTP is sent successfully",HttpStatus.OK);

    }


    //confirming verification code
    @PostMapping("/{mobile_number}/verifyOtp")
    public ResponseEntity<Object> confirmOTP(@PathVariable("mobile_number")String mobile_number, @RequestBody OtpSystem  otpSystem){


        //preventing null exception ERROR

        if(otpSystem.getOtp()==null||otpSystem.getOtp().trim().length()<=0){

            return new ResponseEntity<>("Provide the OTP please ",HttpStatus.BAD_REQUEST);
        }


        if(otpdata.containsKey(mobile_number)){ //checking if the OTP contains the number
            OtpSystem otpSystem1=otpdata.get(mobile_number);

            if(otpSystem1!=null){ //cheking if the the mobile number is null
                  if(otpSystem1.getExpiryTime()>=System.currentTimeMillis()){

                      if(otpSystem.getOtp().equals(otpSystem1.getOtp())){//checking if the OTP are the same


                          //remove the request

                          otpdata.remove(mobile_number);

                          return new ResponseEntity<>("OTP is Confirmed",HttpStatus.OK);

                      }

                      return new ResponseEntity<>("Invalid OTP",HttpStatus.BAD_REQUEST);

                  }
                return new ResponseEntity<>("OTP is Expired",HttpStatus.BAD_REQUEST);

            }

            return new ResponseEntity<>("Something went wrong",HttpStatus.BAD_REQUEST);

        }

           return new ResponseEntity<>("Mobile number not found",HttpStatus.NOT_FOUND);

    }
}
