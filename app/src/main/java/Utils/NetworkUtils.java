package Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author 张云天
 * date on 2019/4/30
 * describe: 网络相关方法
 */
public class NetworkUtils {
    /**
     * 判断是否有网络连接
     * @param context
     * @return 有无网络连接
     */
    public static boolean isNetworkConnected(Context context){
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager != null ? mConnectivityManager.getActiveNetworkInfo() : null;
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
