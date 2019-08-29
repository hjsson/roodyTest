package co.kr.fvn.roodytest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RdSerialPtTh {

    //private int baudrate = 9600;
    //자동 도어 시리얼포트   chmod 777 /dev/ttymxc4
    private String DOOR_SPT = "/dev/ttySC0";
    private RdSerialPt mDoorSp;
    private OutputStream mDoorSp_OutPSt = null;
    private InputStream mDoorSp_InPSt = null;
    //슬라이드 도어 시리얼포트
    private String SLIDE_SPT = "/dev/ttySC1";
    private RdSerialPt mSlideSp;
    private OutputStream mSlideSp_OutPSt = null;
    private InputStream mSlideSp_InPSt = null;
    //도어 락 시리얼포트
    private String LOCK_SPT = "/dev/ttymxc4";
    private RdSerialPt mLockSp;
    private OutputStream mLock_OutPSt = null;
    private InputStream mLock_InPSt = null;

    private RoodyRdThdDoor mRoodyRdThdDoor;
    private RoodyRdThdSlide mRoodyRdThdSlide;
    private RoodyRdThdLock mRoodyRdThdLock;

    private Context context;
    private int baudrate;
    /** 2019-08-02 허성재팀장
     *  LOCK   APP(open)   >>>    ack,ing(0.5초),state(open,close,강제lock)
     *  AUTO   APP(open)   >>>    ack,ing,state(open,충돌)
     *  SLIDE  APP(open)   >>>    ack,ing,state(open,close,충돌)
     * **/

    public RdSerialPtTh(int baudrate, Context context) {
        this.baudrate = baudrate;
        this.context = context;

        // adb shell setenforce 0
        // adb shell chmod 777 /dev/ttySC0
        // adb shell chmod 777 /dev/ttySC1
        // adb shell chmod 777 /dev/ttymxc4

        //시리얼 연결
        if (mDoorSp == null) {
            try {
                mDoorSp = new RdSerialPt(new File(DOOR_SPT), baudrate, 0);
                mDoorSp_OutPSt = mDoorSp.getOutputStream();
                mDoorSp_InPSt = mDoorSp.getInputStream();
                Log.d("Serial Port connect", "mDoorSp==OK");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mSlideSp == null) {
            try {
                mSlideSp = new RdSerialPt(new File(SLIDE_SPT), baudrate, 0);
                mSlideSp_OutPSt = mSlideSp.getOutputStream();
                mSlideSp_InPSt = mSlideSp.getInputStream();
                Log.d("Serial Port connect", "mSlideSp==OK");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mLockSp == null) {
            try {
                mLockSp = new RdSerialPt(new File(LOCK_SPT), baudrate, 0);
                mLock_OutPSt = mLockSp.getOutputStream();
                mLock_InPSt = mLockSp.getInputStream();
                Log.d("Serial Port connect", "mLockSp==OK");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mRoodyRdThdDoor = new RoodyRdThdDoor();
        mRoodyRdThdDoor.setDaemon(true);
        mRoodyRdThdDoor.start();
        mRoodyRdThdSlide = new RoodyRdThdSlide();
        mRoodyRdThdSlide.setDaemon(true);
        mRoodyRdThdSlide.start();
        mRoodyRdThdLock = new RoodyRdThdLock();
        mRoodyRdThdLock.setDaemon(true);
        mRoodyRdThdLock.start();
    }
    //OPEN 프로토콜 D : 메인도어, S : 슬라이드 도어, L : 도어락
    public void sendDate(String target, String type) {
        String sendMsg = "";
        byte[] bytePack = new byte[6];
        bytePack[0] = (byte) 0x24;
        if("D".equals(target)){
            bytePack[1] = (byte) 0x43;
            sendMsg = "Door ";
        }
        if("S".equals(target)){
            bytePack[1] = (byte) 0x42;
            sendMsg = "Slide ";
        }
        if("L".equals(target)){
            bytePack[1] = (byte) 0x41;
            sendMsg = "Lock ";
        }
        if("S".equals(type)){
            bytePack[2] = (byte) 0x30;
        }
        if("O".equals(type)){
            bytePack[2] = (byte) 0x31;
        }
        if("C".equals(type)){
            bytePack[2] = (byte) 0x32;
        }
        bytePack[3] = (byte) (bytePack[0]^bytePack[1]^bytePack[2] | (byte) 0x80);
        bytePack[4] = (byte) 0x0D;
        bytePack[5] = (byte) 0x0A;

        sendMsg = sendMsg+"Send Data: "+String.format("%02x ", bytePack[0]&0xff)+"/"+
                String.format("%02x ", bytePack[1]&0xff)+"/"+
                String.format("%02x ", bytePack[2]&0xff)+"/"+
                String.format("%02x ", bytePack[3]&0xff)+"/"+
                String.format("%02x ", bytePack[4]&0xff)+"/"+
                String.format("%02x ", bytePack[5]&0xff);
        Intent intent = new Intent("diddata");
        intent.putExtra("msg", sendMsg);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        try {
            if (mDoorSp_OutPSt != null && "D".equals(target)) {
                mDoorSp_OutPSt.write(bytePack);
            }
            if (mSlideSp_OutPSt != null && "S".equals(target)) {
                mSlideSp_OutPSt.write(bytePack);
            }
            if (mLock_OutPSt != null && "L".equals(target)) {
                mLock_OutPSt.write(bytePack);
            }
        } catch (IOException e) {
        }
    }

    public void onDataReceived(final byte[] buffer, final int size,String devId) {
        String msg = devId+" Read Data: ";
        for(int i=0;i<size;i++){
            msg = msg+String.format("%02x ", buffer[i]&0xff)+"-";
        }
        Log.d("mD=======", "=="+buffer[3]);
        if(size > 3){
            if(buffer[3] == (byte) 0x30){    //닫힘
                msg = msg+"닫힘";
            }
            if(buffer[3] == (byte) 0x31){    //열리는중
                msg = msg+"열리는중";
            }
            if(buffer[3] == (byte) 0x32){    //닫히는중
                msg = msg+"닫히는중";
            }
            if(buffer[3] == (byte) 0x33){   //열림
                msg = msg+"열림";
            }
            if(buffer[3] == (byte) 0x34){   //충돌
                msg = msg+"충돌";
            }
            if(buffer[3] == (byte) 0x35){   //Ack_nowlege
                msg = msg+"Ack_nowlege";
            }
        }
        Intent intent = new Intent("diddata");
        intent.putExtra("msg", msg);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private class RoodyRdThdDoor extends Thread {
        @Override
        public void run() {
            while (true) {
                Log.d("mDoorSp_InPSt While", "success");
                int size;
                try {
                    byte[] dBuffer = new byte[128];
                    if (mDoorSp_InPSt != null) {
                        size = mDoorSp_InPSt.read(dBuffer);
                        Log.d("mDoorSp_InPSt read Size", "" + size);
                        if (size > 0) {
                            onDataReceived(dBuffer, size,"Door");
                        }
                    }
                } catch (IOException e) {
                    Log.d("mDoorSp_InPSt Fail", "fail");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    private class RoodyRdThdSlide extends Thread {
        @Override
        public void run() {
            while (true) {
                int size;
                try {
                    Log.d("mSlideSp_InPSt While", "success");
                    byte[] sBuffer = new byte[128];
                    if (mSlideSp_InPSt != null) {
                        size = mSlideSp_InPSt.read(sBuffer);
                        Log.d("mSlideSp_InPSt read", "" + size);
                        if (size > 0) {
                            onDataReceived(sBuffer, size,"Slide");
                        }
                    }
                } catch (IOException e) {
                    Log.d("mSlideSp_InPSt Fail", "fail");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    private class RoodyRdThdLock extends Thread {
        @Override
        public void run() {
            while (true) {
                int size;
                try {
                    Log.d("mLock_InPSt While", "success");
                    byte[] lBuffer = new byte[128];
                    if (mLock_InPSt != null) {
                        size = mLock_InPSt.read(lBuffer);
                        Log.d("mLock_InPSt read Size", "" + size);
                        if (size > 0) {
                            onDataReceived(lBuffer, size,"Lock");
                        }
                    }
                } catch (IOException e) {
                    Log.d("mLock_InPSt Fail", "fail");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}