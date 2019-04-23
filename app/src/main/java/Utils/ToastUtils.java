package Utils;

import android.content.Context;
import android.widget.Toast;

import com.example.yischool.register.RegisterActivity;

public class ToastUtils {
    /**
     * 封装Toast提示信息
     * @param context
     * @param message 提示信息
     */
    public static void toastMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
