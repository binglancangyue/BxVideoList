package com.bixin.bxvideolist.model.tools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bixin.bxvideolist.R;
import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.model.bean.VideoBean;
import com.bixin.bxvideolist.model.bean.VideoPlayerBean;
import com.bixin.bxvideolist.view.activity.MyApplication;
import com.bixin.bxvideolist.view.activity.VideoPlayerActivity;

import java.io.File;
import java.util.List;

import static com.bixin.bxvideolist.model.CustomValue.VIDEO_PLAYER_ACTIVITY;
import static com.bixin.bxvideolist.model.CustomValue.VIDEO_PLAYER_PACKAGE_NAME;


/**
 * @author Altair
 * @date :2019.11.22 下午 03:08
 * @description:
 */
public class VideoListOperationTool {
    private static final String TAG = "VideoListOperationTool";
    private String lockPath;
    private String videoPath;

    public VideoListOperationTool() {
        lockPath = StoragePaTool.getStoragePath(true) + "/DVR-BX/LockVideo/";
        videoPath = StoragePaTool.getStoragePath(true) + "/DVR-BX/Video/";
    }

    /**
     * 跳转播放器应用
     *
     * @param data     视频VideoBean
     * @param position 传递的视频的下标
     * @param type     视频类型(加锁和不加锁)
     */
/*    public void openVideoPlayer(VideoBean data, int position, int type) {
        Log.d(TAG, "openVideoPlayer: " + type);
        ComponentName componentName = new ComponentName(VIDEO_PLAYER_PACKAGE_NAME,
                VIDEO_PLAYER_ACTIVITY);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.setComponent(componentName);

        VideoPlayerBean videoPlayerBean = new VideoPlayerBean();
        videoPlayerBean.setName(data.getName());
        videoPlayerBean.setPath(data.getPath());
        videoPlayerBean.setPosition(position);
        videoPlayerBean.setType(type);
        bundle.putParcelable("VideoPlayerBean", videoPlayerBean);

//        bundle.putString(KEY_VIDEO_PATH, data.getPath());
//        bundle.putString(KEY_VIDEO_NAME, data.getName());
//        bundle.putInt(KEY_VIDEO_INDEX, position);
//        bundle.putInt(KEY_VIDEO_TYPE, type);
        intent.putExtras(bundle);
//        MyApplication.getInstance().startActivity(intent);
    }*/

    /**
     * 跳转播放器activity
     *
     * @param data     视频VideoBean
     * @param position 传递的视频的下标
     * @param type     视频类型(加锁和不加锁)
     */
    public void openVideoPlayer(VideoBean data, int position, int type, Context context) {
        Log.d(TAG, "openVideoPlayer: " + type);
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        VideoPlayerBean videoPlayerBean = new VideoPlayerBean();
        videoPlayerBean.setName(data.getName());
        videoPlayerBean.setPath(data.getPath());
        videoPlayerBean.setPosition(position);
        videoPlayerBean.setType(type);
        bundle.putParcelable("VideoPlayerBean", videoPlayerBean);
        intent.putExtras(bundle);
        MyApplication.getInstance().startActivity(intent);
    }

    /**
     * 跳转播放器activity
     *
     * @param data     视频VideoBean
     * @param position 传递的视频的下标
     * @param type     视频类型(加锁和不加锁)
     */
    public Intent openVideoPlayer(VideoBean data, int position, int type) {
        Log.d(TAG, "openVideoPlayer: " + type);
        Intent intent = new Intent(MyApplication.getInstance(), VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        VideoPlayerBean videoPlayerBean = new VideoPlayerBean();
        videoPlayerBean.setName(data.getName());
        videoPlayerBean.setPath(data.getPath());
        videoPlayerBean.setPosition(position);
        videoPlayerBean.setType(type);
        bundle.putParcelable("VideoPlayerBean", videoPlayerBean);
        intent.putExtras(bundle);
        return intent;
    }


    /**
     * 对选中的文件根据操作设置相应的CTR值
     *
     * @param viewId 加锁，解锁，删除按钮
     */
    public void lockFile(int viewId, int currentPage, List<VideoBean> normalVideoList,
                         List<VideoBean> impactVideoList, List<VideoBean> pictureList) {
        List<VideoBean> mData;
        switch (currentPage) {
            case 0:
                mData = normalVideoList;
                break;
            case 1:
                mData = impactVideoList;
                break;
            default:
                mData = pictureList;
                break;
        }
        for (VideoBean videoBean : mData) {
            if (videoBean.getSelect()) {
                if (viewId == R.id.lock) {
                    if (currentPage == 0) {
                        videoBean.setCTR(CustomValue.CTR_LOCK);
                    } else {
                        videoBean.setCTR(CustomValue.CTR_UNLOCK);
                    }
                } else if (viewId == R.id.delete) {
                    videoBean.setCTR(CustomValue.CTR_DELETE);
                }
            }
        }
/*        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getSelect()) {
                if (viewId == R.id.lock) {
                    if (currentPage == 0) {
                        mData.get(i).setCTR(CTR_LOCK);
                    } else {
                        mData.get(i).setCTR(CTR_UNLOCK);
                    }
                } else if (viewId == R.id.delete) {
                    mData.get(i).setCTR(CTR_DELETE);
                }
            }
        }*/

        if (currentPage == 0) {
            fileManagement(normalVideoList, impactVideoList);
        } else if (currentPage == 1) {
            fileManagement(impactVideoList, normalVideoList);
        } else {
            fileManagement(pictureList, pictureList);
        }
//        mInnerHandler.obtainMessage(6, currentPage, 1).sendToTarget();
    }


    /**
     * 根据CTR值对文件操作,删除和更改命名
     *
     * @param currentList 当前list
     * @param changedList 改变后的list
     */
    public void fileManagement(List<VideoBean> currentList, List<VideoBean> changedList) {
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getSelect()) {
                int ctr = currentList.get(i).getCTR();
                if (ctr == CustomValue.CTR_LOCK || ctr == CustomValue.CTR_UNLOCK) {
                    String path;
                    String name;
                    path = currentList.get(i).getPath();
                    name = currentList.get(i).getName();
                    Log.d(TAG, "fileManagement: " + path + "\n" + name + "\n"
                            + currentList.get(i).toString());
                    File file = new File(path);
                    /*if (ctr == CTR_LOCK) {
                        path = path.substring(0, path.length() - 3) + "_impact.ts";
                        name = name.substring(0, name.length() - 3) + "_impact.ts";
                    } else {
                        path = path.substring(0, path.length() - 10) + ".ts";
                        name = name.substring(0, name.length() - 10) + ".ts";
                    }*/
                    if (ctr == CustomValue.CTR_LOCK) {
                        name = "lock_" + name;
                        path = lockPath + name;
                    } else {
                        name = name.replace("lock_", "");
                        path = videoPath + name;
                    }
                    Log.d(TAG, "fileManagement: " + path);
                    File reNameFile = new File(path);
                    file.renameTo(reNameFile);
                    VideoBean bean = new VideoBean();
                    bean.setPath(path);
                    bean.setName(name);
                    bean.setSize(currentList.get(i).getSize());
                    bean.setSelect(false);
                    currentList.remove(i);
                    i--;
                    changedList.add(bean);
                } else if (ctr == CustomValue.CTR_DELETE) {
                    String path = currentList.get(i).getPath();
                    File file = new File(path);
                    file.delete();
                    currentList.remove(i);
                    i--;
                }
            }
        }
        MediaData.listSort(changedList);
//        mInnerHandler.sendEmptyMessageDelayed(CustomValue.CLOSE_DIALOG, 200);
    }
}
