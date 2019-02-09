package teamh.boostcamp.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/*
 * Created By JongSeong */
public class NetworkStateUtil {

    private static final String TAG = "NetworkStateUtil";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NETWORK_CONNECTED, NETWORK_NOT_CONNECTED})

    @interface NetworkState {}

    static final int NETWORK_CONNECTED = 1;
    static final int NETWORK_NOT_CONNECTED = 2;

    private static ConnectivityManager sConnectivityManager = null;

    public static @NetworkState int isNetworkConnected(Context context) {

        // 연결 확인 객체가 없는 경우에만 객체를 작성
        if(sConnectivityManager == null) {
            sConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        NetworkInfo networkInfo = null;

        try {
            networkInfo = sConnectivityManager.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            Log.d(TAG, "Network check util error");
            e.printStackTrace();
        }

        // 결과 반환
        return networkInfo != null && networkInfo.isConnected() ?
                NETWORK_CONNECTED : NETWORK_NOT_CONNECTED;
    }
}