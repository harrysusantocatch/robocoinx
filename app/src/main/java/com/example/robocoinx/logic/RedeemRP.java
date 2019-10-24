package com.example.robocoinx.logic;

import com.example.robocoinx.model.StaticValues;
import com.example.robocoinx.model.view.ProfileView;

public class RedeemRP {
    public String idP;
    public String pointP;
    public String idB;
    public String pointB;
    public int remainingPoint;

    public RedeemRP(ProfileView pp){
        int rpInt = Integer.parseInt(pp.rewardPoint.replace(",", "").trim());
        remainingPoint = parsingPoint(rpInt);
        if(pp.btcBonusTime <= 0) parsingBonus(remainingPoint);
    }

    private void parsingBonus(int rp) {

        if(rp >= StaticValues.POINT_1600){
            idB = StaticValues.FB_500;
            pointB = String.valueOf(StaticValues.POINT_1600);
        }else if(rp >= StaticValues.POINT_800){
            idB = StaticValues.FB_100;
            pointB = String.valueOf(StaticValues.POINT_320);
        }else if(rp >= StaticValues.POINT_400){
            idB = StaticValues.FB_50;
            pointB = String.valueOf(StaticValues.POINT_160);
        }
    }

    private int parsingPoint(int rp) {
        if(rp >= StaticValues.POINT_1200){
            idP = StaticValues.FP100;
            pointP = String.valueOf(StaticValues.POINT_1200);
            return rp - StaticValues.POINT_1200;
        }else if(rp >= StaticValues.POINT_600){
            idP = StaticValues.FP50;
            pointP = String.valueOf(StaticValues.POINT_600);
            return rp - StaticValues.POINT_600;
        }else if(rp >= StaticValues.POINT_300){
            idP = StaticValues.FP25;
            pointP = String.valueOf(StaticValues.POINT_300);
            return rp - StaticValues.POINT_300;
        }else if(rp >= StaticValues.POINT_120){
            idP = StaticValues.FP10;
            pointP = String.valueOf(StaticValues.POINT_120);
            return rp - StaticValues.POINT_120;
        }else if(rp > StaticValues.POINT_12){
            idP = StaticValues.FP1;
            pointP = String.valueOf(StaticValues.POINT_12);
            return rp - StaticValues.POINT_12;
        }else  return 0;
    }
}
