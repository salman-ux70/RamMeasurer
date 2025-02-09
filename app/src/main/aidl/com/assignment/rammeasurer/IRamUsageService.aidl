// IRamUsageService.aidl
package com.assignment.rammeasurer;
import com.assignment.rammeasurer.IRamUsageCallback;

// Declare any non-default types here with import statements

interface IRamUsageService {
      void registerCallback(IRamUsageCallback cb);
      void unregisterCallback();
}