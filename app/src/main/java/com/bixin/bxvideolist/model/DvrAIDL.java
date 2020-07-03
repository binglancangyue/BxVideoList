package com.bixin.bxvideolist.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.bixin.bxvideolist.view.activity.MyApplication;
import com.bx.carDVR.myaidl.FileListInterface;

/**
 * @author Altair
 * @date :2019.12.31 上午 11:17
 * @description:
 */
public class DvrAIDL {
    private Context mContext;
    private FileListInterface listInterface;

    public DvrAIDL() {
        this.mContext = MyApplication.getInstance();
    }

    public void bindAIDLService() {
        Intent intent = new Intent();
        intent.setPackage("com.bx.carDVR");
        intent.setAction("com.bx.carDVR.aidl_service");
        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbingAIDLService() {
        mContext.unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            linkApp = LinkApp.Stub.asInterface(service);
            listInterface = FileListInterface.Stub.asInterface(service);
            if (listInterface == null) {
                Log.d("DvrAIDL", "onServiceConnected:null");
                return;
            }
            try {
                if (listInterface.getPictureList() == null) {
                    Log.d("DvrAIDL", "onServiceConnected:getPictureList null ");
                    return;
                }
//                doGetVideoData(listInterface.getPictureList());
                Log.d("DvrAIDL", "onServiceConnected: " + listInterface.getPictureList().size());
            } catch (RemoteException e) {
                Log.e("DvrAIDL", "onServiceConnected: " + e.getLocalizedMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
