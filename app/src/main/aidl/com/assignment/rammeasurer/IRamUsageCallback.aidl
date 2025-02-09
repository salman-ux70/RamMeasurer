// IRamUsageCallback.aidl
package com.assignment.rammeasurer;

// Declare any non-default types here with import statements

interface IRamUsageCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onRamUsageUpdated(int totalRam, int usedRam);
}