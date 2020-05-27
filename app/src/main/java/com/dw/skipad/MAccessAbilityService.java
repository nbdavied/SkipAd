package com.dw.skipad;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

public class MAccessAbilityService extends AccessibilityService {
    public MAccessAbilityService() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: // 窗口状态改变
                if (event.getPackageName() != null && event.getClassName() != null) {

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

}
