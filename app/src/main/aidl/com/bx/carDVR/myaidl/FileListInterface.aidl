// FileListInterface.aidl
package com.bx.carDVR.myaidl;

// Declare any non-default types here with import statements

interface FileListInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
         Map getVideoList();
         Map getPictureList();
}
