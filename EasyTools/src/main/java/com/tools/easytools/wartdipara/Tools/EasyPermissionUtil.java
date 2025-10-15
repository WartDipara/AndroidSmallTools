package com.tools.easytools.wartdipara.Tools;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.tools.easytools.wartdipara.EasyPermission.PermissionInstance;

import java.util.List;

/**
 * 權限申請工具類，封裝 PermissionInstance 和 SupportFragment 的使用。
 */
public class EasyPermissionUtil {
    /**
     * 權限申請回調接口，外部直接實現即可。
     */
    public interface PermissionCallback {
        /**
         * 權限申請結果回調
         * @param allGranted 是否全部授權
         * @param deniedPermissions 被拒絕的權限列表
         */
        void onResult(boolean allGranted, List<String> deniedPermissions);
    }

    /**
     * 申請權限
     * @param activity FragmentActivity 實例
     * @param callback 權限回調
     * @param permissions 權限列表
     */
    public static void requestPermissions(FragmentActivity activity, PermissionCallback callback, String... permissions) {
        PermissionInstance.getInstance().request(activity, (allGranted, deniedPermissions) -> {
            if (callback != null) {
                callback.onResult(allGranted, deniedPermissions);
            }
        }, permissions);
    }

    /**
     * 申請權限（支持 Fragment）
     * @param fragment Fragment 實例
     * @param callback 權限回調
     * @param permissions 權限列表
     */
    public static void requestPermissions(Fragment fragment, PermissionCallback callback, String... permissions) {
        PermissionInstance.getInstance().request(fragment, (allGranted, deniedPermissions) -> {
            if (callback != null) {
                callback.onResult(allGranted, deniedPermissions);
            }
        }, permissions);
    }

}
