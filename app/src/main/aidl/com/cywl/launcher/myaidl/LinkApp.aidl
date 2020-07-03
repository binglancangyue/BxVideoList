// LinkApp.aidl
package com.cywl.launcher.myaidl;

//import com.cywl.launcher.myaidl.Video;
// Declare any non-default types here with import statements
interface LinkApp {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
        List<String> basicTypes();
        Map getVideoList();

}
