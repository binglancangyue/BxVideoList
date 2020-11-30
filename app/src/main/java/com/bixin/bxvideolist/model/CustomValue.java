package com.bixin.bxvideolist.model;

import com.bixin.bxvideolist.model.tools.StoragePaTool;

public class CustomValue {
    public static final int LOCK_FILE = 2;
    public static final int DELETE_FILE = 3;
    public static final int CLOSE_DIALOG = 4;
    public static final int LOADING_DATA = 5;

    public static final String VIDEO_PLAYER_PACKAGE_NAME = "com.cywl.launcher.videoplayer";
    public static final String VIDEO_PLAYER_ACTIVITY = VIDEO_PLAYER_PACKAGE_NAME +
            ".VideoPlayerActivity";

    public static final String KEY_VIDEO_PATH = "video_path";
    public static final String KEY_VIDEO_NAME = "video_name";
    public static final String KEY_VIDEO_INDEX = "video_index";
    public static final String KEY_VIDEO_TYPE = "video_type";//normal or lock

    public final static int CTR_DELETE = 0;
    public final static int CTR_LOCK = 1;
    public final static int CTR_UNLOCK = 2;

    public static final String LOCAL_PATH = "/storage/emulated/0/DCIM/Camera";
    //    public static final String SDCARD_PATH = "/storage/0000-006F/DVR-BX";
    // storage/0000-006F/DVR-BX
    public static final String SDCARD_PATH = StoragePaTool.getStoragePath(true);

    public static final int VIDEO_TYPE_NORMAL = 0;
    public static final int VIDEO_TYPE_LOCK = 1;

    public static final String KEY_VIDEO_PLAYER_BEAN = "video_player_bean";
    public static final String ACTION_STOP_RECORD = "com.bixin.bxvideolist.action.stop_recording";

    public static final int HANDLE_EXIT_APP = 1;
    public static final int UPDATE_BUTTON_BG = 2;

    public static final boolean IS_KD003 = true;
    public static final boolean IS_3IN = true;
    public static final boolean IS_966 = false;
}
