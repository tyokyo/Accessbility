package com.example.dell.accessbility;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ContentValues;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Calendar;
import java.util.Locale;

@SuppressLint("NewApi")
public class MyAccessibility extends AccessibilityService {
	private static final String TAG = "MyAccessibility";
	String[] PACKAGES = { "com.android.settings" };

	@Override
	protected void onServiceConnected() {
//		Log.i(TAG, "config success!");
//		AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
//		// accessibilityServiceInfo.packageNames = PACKAGES;
//		accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//		accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
//		accessibilityServiceInfo.notificationTimeout = 1000;
//		setServiceInfo(accessibilityServiceInfo);
	}
	private void printNotificationInfo(AccessibilityEvent event){
		long timestamp = Calendar.getInstance().getTimeInMillis();
		String sourcePackageName = (String) event.getPackageName();
		String message = "";
		for (CharSequence text : event.getText()) {
			message += text + "\n";
		}
		if (message.length() > 0) {
			message = message.substring(0, message.length() - 1);
		}
		Parcelable parcelable = event.getParcelableData();
		if (!(parcelable instanceof Notification)) {
			ContentValues cv = new ContentValues();
			cv.put("package", sourcePackageName);
			cv.put("message", message);
			cv.put("timestap", timestamp);
			Log.i(TAG,"Notification-msg is "+message);
		}
	}
	private String getEventTypeString(AccessibilityEvent paramAccessibilityEvent)
	{
		int i = paramAccessibilityEvent.getEventType();
		switch (i)
		{
			default:
				Locale localLocale = Locale.getDefault();
				Object[] arrayOfObject = new Object[1];
				arrayOfObject[0] = Integer.valueOf(i);
				return String.format(localLocale, "unknown (%d)", arrayOfObject);
			case 16384:
				return "TYPE_ANNOUNCEMENT";
			case 524288:
				return "TYPE_GESTURE_DETECTION_END";
			case 262144:
				return "TYPE_GESTURE_DETECTION_START";
			case 64:
				return "TYPE_NOTIFICATION_STATE_CHANGED";
			case 1024:
				return "TYPE_TOUCH_EXPLORATION_GESTURE_END";
			case 512:
				return "TYPE_TOUCH_EXPLORATION_GESTURE_START";
			case 2097152:
				return "TYPE_TOUCH_INTERACTION_END";
			case 1048576:
				return "TYPE_TOUCH_INTERACTION_START";
			case 32768:
				return "TYPE_VIEW_ACCESSIBILITY_FOCUSED";
			case 65536:
				return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";
			case 1:
				return "TYPE_VIEW_CLICKED";
			case 8:
				return "TYPE_VIEW_FOCUSED";
			case 128:
				return "TYPE_VIEW_HOVER_ENTER";
			case 256:
				return "TYPE_VIEW_HOVER_EXIT";
			case 2:
				return "TYPE_VIEW_LONG_CLICKED";
			case 4096:
				return "TYPE_VIEW_SCROLLED (" + paramAccessibilityEvent.getScrollX() + "," + paramAccessibilityEvent.getScrollY() + ") ";
			case 4:
				return "TYPE_VIEW_SELECTED";
			case 16:
				return "TYPE_VIEW_TEXT_CHANGED";
			case 8192:
				return "TYPE_VIEW_TEXT_SELECTION_CHANGED";
			case 131072:
				return "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
			case 4194304:
				return "TYPE_WINDOWS_CHANGED";
			case 2048:
				return "TYPE_WINDOW_CONTENT_CHANGED";
			case 32:
		}
		return "TYPE_WINDOW_STATE_CHANGED";
	}
	private void fillListViewText(AccessibilityNodeInfo paramAccessibilityNodeInfo)
	{
		for (int i = 0; i < paramAccessibilityNodeInfo.getChildCount(); i++)
		{
			AccessibilityNodeInfo localAccessibilityNodeInfo = paramAccessibilityNodeInfo.getChild(i);
			if ((localAccessibilityNodeInfo != null) && (localAccessibilityNodeInfo.getText() != null)){
				Log.i(TAG,localAccessibilityNodeInfo.getText().toString());
			}
			fillListViewText(localAccessibilityNodeInfo);
		}
	}
	private void fillList(AccessibilityNodeInfo paramAccessibilityNodeInfo)
	{
		if (paramAccessibilityNodeInfo != null){
			if (paramAccessibilityNodeInfo.getClassName().toString().equals("android.widget.ListView")){
				fillListViewText(paramAccessibilityNodeInfo);
				for (int i = 0; i < paramAccessibilityNodeInfo.getChildCount(); i++)
				{
					AccessibilityNodeInfo localAccessibilityNodeInfo = paramAccessibilityNodeInfo.getChild(i);
					if ((localAccessibilityNodeInfo != null) && (localAccessibilityNodeInfo.getText() != null)){
						Log.i(TAG,localAccessibilityNodeInfo.getText().toString());
					}
					fillList(localAccessibilityNodeInfo);
				}
			}
		}

	}
	public void getAllTextOnScreen(AccessibilityEvent paramAccessibilityEvent)
	{
		AccessibilityNodeInfo localAccessibilityNodeInfo = paramAccessibilityEvent.getSource();
		if ((localAccessibilityNodeInfo == null) || (paramAccessibilityEvent.getPackageName().equals("com.android.systemui")) || (paramAccessibilityEvent.getPackageName().equals("com.cybercom.doroautolizertestcaserecorder")))
			return;
		while (localAccessibilityNodeInfo.getParent() != null){
			localAccessibilityNodeInfo = localAccessibilityNodeInfo.getParent();
			fillList(localAccessibilityNodeInfo);
		}
		Log.d("uiautomatorRecorder", "All text are added");
	}
	private AccessibilityNodeInfo getListItemNodeInfo(AccessibilityNodeInfo source) {

		AccessibilityNodeInfo current = source;
		while (true) {
			AccessibilityNodeInfo parent = current.getParent();
			if(parent == null) {
				return null;
			}
			Log.i(TAG,parent.toString());
			//回收节点
			AccessibilityNodeInfo oldcurrent = current;
			current=parent;
			oldcurrent.recycle();
		}

	}
	@SuppressLint("NewApi")
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		int eventType = event.getEventType();
		String eventText = "";
		switch (eventType) {
		case AccessibilityEvent.TYPE_VIEW_CLICKED:
			try {
				printEventLog(event);
				findAllResourceIds(event);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			eventText = "TYPE_VIEW_CLICKED";
			break;
		case AccessibilityEvent.TYPE_VIEW_FOCUSED:
			eventText = "TYPE_VIEW_FOCUSED";
			break;
		case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
			eventText = "TYPE_VIEW_LONG_CLICKED";
			break;
		case AccessibilityEvent.TYPE_VIEW_SELECTED:
			eventText = "TYPE_VIEW_SELECTED";
			break;
		case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
			eventText = "TYPE_VIEW_TEXT_CHANGED";
			break;
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			printNotificationInfo(event);
			eventText = "TYPE_WINDOW_STATE_CHANGED";
			break;
		case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
			eventText = "TYPE_NOTIFICATION_STATE_CHANGED";
			Log.i(TAG,"AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED");
			break;
		case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
			eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
			break;
		case AccessibilityEvent.TYPE_ANNOUNCEMENT:
			eventText = "TYPE_ANNOUNCEMENT";
			break;
		case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
			eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
			break;
		case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
			eventText = "TYPE_VIEW_HOVER_ENTER";
			break;
		case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
			eventText = "TYPE_VIEW_HOVER_EXIT";
			break;
		case AccessibilityEvent.TYPE_VIEW_SCROLLED:
			eventText = "TYPE_VIEW_SCROLLED";
			break;
		case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
			eventText = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
			break;
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			eventText = "TYPE_WINDOW_CONTENT_CHANGED";
			break;
		}
	}
	private void printEventLog(AccessibilityEvent event) {
		Log.i(TAG, "-------------------------------------------------------------");
		int eventType = event.getEventType(); //事件类型

		Log.i(TAG, "PackageName:" + event.getPackageName() + ""); // 响应事件的包名
		Log.i(TAG, "Source Class:" + event.getClassName() + ""); // 事件源的类名
		Log.i(TAG, "Description:" + event.getContentDescription()+ ""); // 事件源描述
		Log.i(TAG, "Event Type(int):" + eventType + "");
		Log.i(TAG, "Source:" + event.getSource().toString());
		Log.i(TAG, "id:" + event.getSource().getViewIdResourceName() + ""); // 响应事件的id
		switch (eventType) {
			case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
				Log.i(TAG, "event type:TYPE_NOTIFICATION_STATE_CHANGED");
				break;
			case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
				Log.i(TAG, "event type:TYPE_WINDOW_STATE_CHANGED");
				break;
			case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED://View获取到焦点
				Log.i(TAG, "event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
				break;
			case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
				Log.i(TAG, "event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
				break;
			case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
				Log.i(TAG, "event type:TYPE_GESTURE_DETECTION_END");
				break;
			case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
				Log.i(TAG, "event type:TYPE_WINDOW_CONTENT_CHANGED");
				break;
			case AccessibilityEvent.TYPE_VIEW_CLICKED:
				Log.i(TAG, "event type:TYPE_VIEW_CLICKED");
				findAllResourceIds(event);
				break;
			case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
				Log.i(TAG, "event type:TYPE_VIEW_TEXT_CHANGED");
				break;
			case AccessibilityEvent.TYPE_VIEW_SCROLLED:
				Log.i(TAG, "event type:TYPE_VIEW_SCROLLED");
				break;
			case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
				Log.i(TAG, "event type:TYPE_VIEW_TEXT_SELECTION_CHANGED");
				break;
			default:
				Log.i(TAG, "no listen event");
		}

		for (CharSequence txt : event.getText()) {
			Log.i(TAG, "text:" + txt);
		}

		Log.i(TAG, "-------------------------------------------------------------");
	}
	public void findAllResourceIds(AccessibilityEvent paramAccessibilityEvent)
	{
		AccessibilityNodeInfo localAccessibilityNodeInfo1 = paramAccessibilityEvent.getSource();

		if ((localAccessibilityNodeInfo1 == null) || (paramAccessibilityEvent.getPackageName().equals("com.android.systemui")) || (paramAccessibilityEvent.getPackageName().equals("com.cybercom.doroautolizertestcaserecorder")))
			return;
		for (
				AccessibilityNodeInfo localAccessibilityNodeInfo2 = localAccessibilityNodeInfo1;
				localAccessibilityNodeInfo2.getParent() != null;
				localAccessibilityNodeInfo2 = localAccessibilityNodeInfo2.getParent()
				);
			Log.i(TAG, ""+paramAccessibilityEvent.getSource().getViewIdResourceName());
	}
	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

}
