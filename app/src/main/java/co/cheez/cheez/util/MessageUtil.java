package co.cheez.cheez.util;

import android.widget.Toast;

import co.cheez.cheez.App;

/**
 * Created by jiho on 4/7/15.
 */
public class MessageUtil {
    public static void showDefaultErrorMessage() {
        showMessage("오류가 발생했습니다. 잠시 후에 다시 시도해주세요 :(");
    }
    public static void showMessage(String message) {
        Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
