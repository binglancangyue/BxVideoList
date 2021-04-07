package com.bixin.bxvideolist.model.tools;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.bixin.bxvideolist.model.CustomValue;
import com.bixin.bxvideolist.model.bean.VideoBean;
import com.bixin.bxvideolist.view.activity.MyApplication;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaData {
    private List<VideoBean> normalVideoList = new ArrayList<>();
    private List<VideoBean> impactVideoList = new ArrayList<>();
    private List<VideoBean> pictureList = new ArrayList<>();
    private ArrayList<File> fileArrayList = new ArrayList<>();

    private String getFileSize(File file) {
        String size;
        if (file.exists() && file.isFile()) {
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS);
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) + "GB";
            }
        } else if (file.exists() && file.isDirectory()) {
            size = "";
        } else {
            size = "0BT";
        }
        return size;
    }

    private String getFileSize(long fileSize) {
        String size;
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileSize < 1024) {
            size = df.format((double) fileSize);
        } else if (fileSize < 1048576) {
            size = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            size = df.format((double) fileSize / 1048576) + "MB";
        } else {
            size = df.format((double) fileSize / 1073741824) + "GB";
        }
        return size;
    }

    /**
     * 区分次数
     *
     * @param theFirst
     */
    public void rescan(boolean theFirst) {
        String SDCardPath = CustomValue.SDCARD_PATH + "/DVR-BX";
        Log.d("test11", "rescan: "+SDCardPath);
        List<VideoBean> normalVideoTempList = new ArrayList<>();
        List<VideoBean> impactVideoTempList = new ArrayList<>();
        List<VideoBean> pictureTempList = new ArrayList<>();

        File fileSDCard = new File(SDCardPath);
        ArrayList<File> files = new ArrayList<>();
        if (fileSDCard.exists()) {
            if (fileSDCard.exists()) {
                refreshFileList(SDCardPath, files);
            }
            //排序
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o2.getName().compareTo(o1.getName());
                }
            });
            int listSize = files.size();
            if (theFirst) {
                if (listSize > 30) {
                    listSize = 30;
                }
            }
            for (int i = 0; i < listSize; i++) {
                File file = files.get(i);
                if (!file.exists()) {
                    continue;
                }
                if (file.length() == 0) {
                    file.delete();
                    continue;
                }
                VideoBean videoBean = new VideoBean();
                String path = file.getPath();
                String size = getFileSize(file);
                String name = file.getName();
                videoBean.setPath(path);
                videoBean.setName(name);
                videoBean.setSize(size);
                videoBean.setSelect(false);
                if (name.endsWith(".mp4")) {
                    if (!name.contains("lock")) {
                        normalVideoTempList.add(videoBean);
                    } else {
                        impactVideoTempList.add(videoBean);
                    }
                }
                if (name.endsWith(".jpg")) {
                    pictureTempList.add(videoBean);
                }
            }
            clearData();
            normalVideoList.addAll(normalVideoTempList);
            impactVideoList.addAll(impactVideoTempList);
            pictureList.addAll(pictureTempList);
        } else {
            clearData();
        }
    }

    public void rescan() {
        String SDCardPath = CustomValue.SDCARD_PATH;
        Log.d("ag", "rescan:SDCardPath " + SDCardPath);
//        String SDCardPath = StoragePaTool.getDVRPath();
        List<VideoBean> normalVideoTempList = new ArrayList<>();
        List<VideoBean> impactVideoTempList = new ArrayList<>();
        List<VideoBean> pictureTempList = new ArrayList<>();
        if (SDCardPath == null) {
            return;
        }
        File fileSDCard = new File(SDCardPath);
        ArrayList<File> files = new ArrayList<>();
        if (fileSDCard.exists()) {
            if (fileSDCard.exists()) {
                refreshFileList(SDCardPath, files);
            }
            Log.d("aa", "rescan: " + files.size());
            //排序
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o2.getName().compareTo(o1.getName());
                }
            });
            for (File file : files) {
                if (!file.exists()) {
                    continue;
                }
                if (file.length() == 0) {
                    file.delete();
                    continue;
                }
                VideoBean videoBean = new VideoBean();
                String path = file.getPath();
                String size = getFileSize(file);
                String name = file.getName();
                videoBean.setPath(path);
                videoBean.setName(name);
                videoBean.setSize(size);
                videoBean.setSelect(false);
                if (name.endsWith(".mp4")) {
                    if (!name.contains("impact")) {
                        normalVideoTempList.add(videoBean);
                    } else {
                        impactVideoTempList.add(videoBean);
                    }
                }
                if (name.endsWith(".jpg")) {
                    pictureTempList.add(videoBean);
                }
            }
            clearData();
            normalVideoList.addAll(normalVideoTempList);
            impactVideoList.addAll(impactVideoTempList);
            pictureList.addAll(pictureTempList);
        } else {
            clearData();
        }
    }

    private void refreshFileList(String strPath, ArrayList<File> freelist) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                refreshFileList(file.getAbsolutePath(), freelist);
            } else {
                freelist.add(file);
            }
        }
    }


    private void clearData() {
        normalVideoList.clear();
        impactVideoList.clear();
        pictureList.clear();
    }

//    public static Bitmap fin_Bitmap(List<VideoBean> list, String path) {
//        Log.d("aaa", "fin_Bitmap: " + list.size() + " " + path);
//        for (int i = 0; i < list.size(); i++) {
//            if (path.equals(list.get(i).getPath()))
//                return list.get(i).getThumbnail();
//        }
//        return null;
//    }

    public List<VideoBean> getNormalVideoList() {
        return normalVideoList;
    }

    public List<VideoBean> getImpactVideoList() {
        return impactVideoList;
    }

    public List<VideoBean> getPictureList() {
        return pictureList;
    }

    public static void listSort(List<VideoBean> resultList) {
        // resultList是需要排序的list，其内放的是Map
        // 返回的结果集
        Collections.sort(resultList, new Comparator<VideoBean>() {

            public int compare(VideoBean o1, VideoBean o2) {
                //o1，o2是list中的Map，可以在其内取得值，按其排序，此例为升序，s1和s2是排序字段值
                String s1 = o1.getName();
                String s2 = o2.getName();
                return s2.compareTo(s1);
            }
        });
    }

    private String getSDCardPath() {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录 }
            return sdDir.toString();
        }
        return null;
    }

    public void queryData() {
        queryVideo();
        queryPicture();
    }

    private void queryPicture() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = MyApplication.getInstance().getContentResolver();
        String[] s = {
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.SIZE};
        //只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, s,
                null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        VideoBean videoBean;
        pictureList.clear();
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                videoBean = new VideoBean();
                String path = mCursor.getString(0);
                if (path.contains(CustomValue.SDCARD_PATH + "/DVR-BX")) {
                    String size = getFileSize(mCursor.getLong(2));
                    String name = mCursor.getString(1);
                    videoBean.setPath(path);
                    videoBean.setName(name);
                    videoBean.setSize(size);
                    videoBean.setSelect(false);
                    pictureList.add(videoBean);
                    Log.d("TAG", "queryPicture: " + name + "\n");
                }
            }
            mCursor.close();
        }

    }

    private void queryVideo() {
        Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = MyApplication.getInstance().getContentResolver();
        String[] s = {
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.SIZE};
        Cursor mCursor = mContentResolver.query(mImageUri, s,
                null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        VideoBean videoBean;
        normalVideoList.clear();
        impactVideoList.clear();
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                videoBean = new VideoBean();
                String path = mCursor.getString(0);
                if (path.contains(CustomValue.SDCARD_PATH + "/DVR-BX")) {
                    String size = getFileSize(mCursor.getLong(2));
                    String name = mCursor.getString(1);
                    videoBean.setPath(path);
                    videoBean.setName(name);
                    videoBean.setSize(size);
                    videoBean.setSelect(false);
                    Log.d("TAG", "queryVideo: " + name + "\n");
                    if (name.endsWith(".mp4")) {
                        if (!name.contains("impact")) {
                            normalVideoList.add(videoBean);
                        } else {
                            impactVideoList.add(videoBean);
                        }
                    }
                }
            }
            mCursor.close();
        }
    }

}
