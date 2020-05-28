package com.dw.skipad.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Service;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class MAccessAbilityService extends AccessibilityService {
    public MAccessAbilityService() {
    }
    private List<String> keyWordList;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        keyWordList = new ArrayList<>();
        keyWordList.add("跳过");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: // 窗口状态改变
                Log.d("event", "WINDOW_STATE_CHANGED");
                //readWindowContent(event);
                findSkipButtonByText(getRootInActiveWindow());
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                //Log.d("event", "WINDOW_CONTENT_CHANGED");
                //readWindowContent(event);
                findSkipButtonByText(getRootInActiveWindow());
                break;
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 查找所有
     * 的控件
     */
    private void findAllNode(List<AccessibilityNodeInfo> roots, List<AccessibilityNodeInfo> list) {
        ArrayList<AccessibilityNodeInfo> temList = new ArrayList<>();
        for (AccessibilityNodeInfo e : roots) {
            if (e == null) continue;
            list.add(e);
            for (int n = 0; n < e.getChildCount(); n++) {
                temList.add(e.getChild(n));
            }
        }
        if (!temList.isEmpty()) {
            findAllNode(temList, list);
        }
    }

    private void readWindowContent(AccessibilityEvent event) {
        if (event.getPackageName() != null && event.getClassName() != null) {
            String packageName = (String) event.getPackageName();
            String className = (String) event.getClassName();
            Log.i("package", packageName);
            Log.i("classname", className);
            if (packageName.startsWith("com.android") || packageName.startsWith("com.sec.android")) {
                return;
            }
            AccessibilityNodeInfo window = getRootInActiveWindow();
            if (window != null) {
                List<AccessibilityNodeInfo> roots = new ArrayList<>();
                roots.add(window);
                List<AccessibilityNodeInfo> list = new ArrayList<>();
                findAllNode(roots, list);
                for (AccessibilityNodeInfo node : list) {
                    String nodeClass = (String) node.getClassName();
                    if (nodeClass != null) {
                        Log.i("view class name", nodeClass);
                    }
                    if (node.getViewIdResourceName() != null) {
                        Log.i("view id", node.getViewIdResourceName());
                    }
                    CharSequence text = node.getText();
                    if (text != null) {
                        Log.i("node text", text.toString());
//                        if (text.toString().contains("跳过")) {
//                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        }

                    }
                    //node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }

        }
    }
    /**
     * 自动查找启动广告的
     * “跳过”的控件
     */
    private void findSkipButtonByText(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return;
        for (int n = 0; n < keyWordList.size(); n++) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(keyWordList.get(n));
            if (!list.isEmpty()) {
                for (AccessibilityNodeInfo e : list) {
                    if (!e.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                        if (!e.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Rect rect = new Rect();
                            e.getBoundsInScreen(rect);
                            click(rect.centerX(), rect.centerY(), 0, 20);
                        }
                    }
                    e.recycle();
                }
                //is_state_change_c = false;
                Log.i("success", "成功跳过");
                return;
            }

        }
        nodeInfo.recycle();
    }
    /**
     * 模拟
     * 点击
     */
    private boolean click(int X, int Y, long start_time, long duration) {
        Path path = new Path();
        path.moveTo(X, Y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            GestureDescription.Builder builder = new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(path, start_time, duration));
            return dispatchGesture(builder.build(), null, null);
        } else {
            return false;
        }
    }
}
