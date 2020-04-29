package com.khuong.callvideo_khuong_28042020;

import android.app.ActivityManager;
import android.app.Presentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;
import java.util.Map;

import static com.khuong.callvideo_khuong_28042020.IncomingCallScreenActivity.EXTRA_ID;
import static com.khuong.callvideo_khuong_28042020.IncomingCallScreenActivity.MESSAGE_ID;

public class SinchServer extends Service {
    static final String APP_KEY = "enter-application-key";
    static final String APP_SECRET = "enter-application-secret";
    static final String ENVIRONMENT = "clientapi.sinch.com";
    // tham số khai báo khởi tạo để cấp quyên
    public static final int MESS_PERMISSONS_NEEED = 1;
    public static final String REQUIRED_PERMISSION = "REQUIRED_PESMISSION";
    public static final String MESSENGER = "MESSENGER";
    // lop ke thua Parcelable có thể gửi thông qua các ACtivity;
    private Messenger messenger;
    // call id dùng với Sinch
    public static final String CALL_ID = "CALL_ID";
    //PersistedSettings dùng để kiểm tra xemm User dó có đc cấp quyền hay không;
    private PersistedSettings msSettings;
    //  khai cáo lớp SinchInterfar
    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
    // khai báo lớp Interface StartFailedListener
    private StartFailedListener mListener;
    String TAG = "VaKhuong_SinchServer";

    private SinchClient mSinchClient;// khaibáo 1 SinchClient;


    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    // chỉ chạy duy nhất 1 lần
    @Override
    public void onCreate() {
        super.onCreate();
        msSettings = new PersistedSettings(getApplicationContext());// quyền
        attemptAutoStart();
    }

    private void createClient(String userName) {// khởi tạo SinchClient
        mSinchClient = (SinchClient) Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();
        // call and push call
        mSinchClient.setSupportCalling(true);
        mSinchClient.setSupportManagedPush(true);
        // client lắng nghe các sự kiện của Sinch
        mSinchClient.addSinchClientListener(new MySinchClientListener());// bình thường new... nhưng thay vì thế thì khởi tạo  1 lớp kế thừa cái lớp cần khởi tạo
        // lắng nghe các sự kiện client call
        mSinchClient.getCallClient().addCallClientListener(new MySinchCallClientListener());// cũng vậy
        // push noti
        mSinchClient.setPushNotificationDisplayName("User " + userName);

    }

    private class MySinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "onIncomingCall: vào IncomNing call ");
            Intent intent = new Intent(SinchServer.this, IncomingCallScreenActivity.class);
            intent.putExtra(EXTRA_ID, MESSAGE_ID);
            intent.putExtra(CALL_ID, call.getCallId());
           


        }
    }

    // kieem tra xsem ung dung co ddang ở trạng thái Forgroud không
    private boolean isAppOnForeground(Context context) {
        //khai lớp activityManager.
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return false;

    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientStarted(SinchClient sinchClient) {
            Log.d(TAG, "onClientStarted: Sinch được stated");
            if (mListener != null) {
                mListener.onStarted();
            }
        }

        @Override
        public void onClientStopped(SinchClient sinchClient) {
            Log.d(TAG, "onClientStopped: Sinch client On Stoped ");
        }

        @Override
        public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
            if (mListener != null) {
                mListener.onStartFailed(sinchError);
            }
            mSinchClient.terminate();
            mSinchClient = null;
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {
        }

        @Override
        public void onLogMessage(int i, String s, String s1) {
            switch (i) {
                case Log.DEBUG:
                    Log.d(s, s1);
                    break;
                case Log.ERROR:
                    Log.d(s, s1);
                    break;
                case Log.INFO:
                    Log.d(s, s1);
                    break;
                case Log.VERBOSE:
                    Log.d(s, s1);
                    break;
                case Log.WARN:
                    Log.d(s, s1);
                    break;
            }
        }
    }

    //  service của bạn chỉ cần sử dụng cho ứng dụng local và không cần phải làm việc với các processes khác, bạn có thể sử dụng Binder class, nó sẽ cung cấp cho client quyền truy cập trực tiếp vào các phương thức public trong service
    private class SinchServiceInterface extends Binder {

        public Call callUserVideo(String userId) {
            return mSinchClient.getCallClient().callUserVideo(userId);
        }

        public String getUserName() {
            return msSettings.getUserName();
        }

        public void setUserName(String UserId) {
            msSettings.setUserName(UserId);
        }

        // tự động chạy lại khi được cấp đủ quyền
        public void retryStartAfterPermissionGranted() {
            SinchServer.this.attemptAutoStart();
        }

        public boolean isStarted() {
            return SinchServer.this.isStarted();
        }

        public void startClient() {
            start();
        }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return mSinchClient.getCallClient().getCall(callId);
        }

        public VideoController getVideoController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getVideoController();

        }

        public AudioController getAudioController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getAudioController();
        }

        // chuyển tiếp Tải trọng thông báo đẩy từ xa
        public NotificationResult relayRemotePushNotificationPayload(final Map playLoad) {
            if (!hasUsername()) {
                Log.d(TAG, "relayRemotePushNotificationPayload: Không thể chuyển tiếp đẩy thông báo lên Firebase");
                return null;
            }
            return mSinchClient.relayRemotePushNotificationPayload(playLoad);
        }

    }

    private boolean hasUsername() {
        if (msSettings.getUserName().isEmpty()) {
            Log.d(TAG, "không thể khởi động Sinch vì tên uer trống: ");
            return false;
        }
        return true;
    }

    public interface StartFailedListener {
        void onStartFailed(SinchError error);

        void onStarted();
    }

    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.terminateGracefully();
            mSinchClient = null;
        }
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    private void attemptAutoStart() {
        if (messenger != null) {
            start();// bắt đầu
        }
    }

    private void start() {
// tại đây bắt đầu check quyền Menifesttrong android
    }


    private class PersistedSettings {
        //Share dùng để lưu các quyền đã cấp
        private SharedPreferences mStore;
        // key
        private final static String PREF_KEY = "Quyen_Sinch";

        public PersistedSettings(Context context) {// khỏi tạo
            mStore = context.getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        }

        public String getUserName() {
            return mStore.getString("Username", "");
        }

        public void setUserName(String User) {

            SharedPreferences.Editor editor = mStore.edit();
            editor.putString("Username", User);
            editor.commit();


        }


    }
}
