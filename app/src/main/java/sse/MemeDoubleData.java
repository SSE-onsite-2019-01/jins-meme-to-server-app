package sse;

import com.jins_jp.meme.MemeRealtimeData;

/**
 * Created by sei on 2018/03/08.
 */

public class MemeDoubleData {

    double eyeMoveUp = 0;
    double eyeMoveDown = 0;
    double eyeMoveLeft = 0;
    double eyeMoveRight = 0;
    double blinkSpeed = 0;
    double blinkStrength = 0;
    double walking = 0;
    double roll = 0;
    double pitch = 0;
    double yaw = 0;
    double accX = 0;
    double accY = 0;
    double accZ = 0;
    int dataNum = 0;

    public MemeDoubleData copy() {
        MemeDoubleData data = new MemeDoubleData();
        data.eyeMoveUp = eyeMoveUp;
        data.eyeMoveDown = eyeMoveDown;
        data.eyeMoveLeft = eyeMoveLeft;
        data.eyeMoveRight = eyeMoveRight;
        data.blinkSpeed = blinkSpeed;
        data.blinkStrength = blinkStrength;
        data.walking = walking;
        data.roll = roll;
        data.pitch = pitch;
        data.yaw = yaw;
        data.accX = accX;
        data.accY = accY;
        data.accZ = accZ;
        if(dataNum==0){
            data.dataNum = 1;
        }else {
            data.dataNum = dataNum;
        }
        return data;
    }

    public void addMemeDoubleData(MemeRealtimeData d) {
        eyeMoveUp += d.getEyeMoveUp();
        eyeMoveDown += d.getEyeMoveDown();
        eyeMoveLeft += d.getEyeMoveLeft();
        eyeMoveRight += d.getEyeMoveRight();
        blinkSpeed += d.getBlinkSpeed();
        blinkStrength += d.getBlinkStrength();
        walking += d.isWalking() ? 1 : 0;
        roll += d.getRoll();
        pitch += d.getPitch();
        yaw += d.getYaw();
        accX += d.getAccX();
        accY += d.getAccY();
        accZ += d.getAccZ();
        dataNum++;
    }

    public double getAverageEyeMoveUp() {
        return eyeMoveUp / dataNum;
    }

    public double getAverageEyeMoveDown() {
        return eyeMoveDown / dataNum;
    }

    public double getAverageEyeMoveLeft() {
        return eyeMoveLeft / dataNum;
    }

    public double getAverageEyeMoveRight() {
        return eyeMoveRight / dataNum;
    }

    public double getAverageBlinkSpeed() {
        return blinkSpeed / dataNum;
    }

    public double getAverageBlinkStrength() {
        return blinkStrength / dataNum;
    }

    public double getAverageWalking() {
        return walking / dataNum;
    }

    public double getAverageRoll() {
        return roll / dataNum;
    }

    public double getAveragePitch() {
        return pitch / dataNum;
    }

    public double getAverageYaw() {
        return yaw / dataNum;
    }

    public double getAverageAccX() {
        return accX / dataNum;
    }

    public double getAverageAccY() {
        return accY / dataNum;
    }

    public double getAverageAccZ() {
        return accZ / dataNum;
    }
}
