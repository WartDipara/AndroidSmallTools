package com.tools.easytools.wartdipara.EasyPermission;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Invisible fragment to handle permission requests.
 */
public class SupportFragment extends Fragment {
    public interface PermissionCallback {
        /**
         * Callback for permission request result
         *
         * @param allGranted        true if all request are granted
         * @param deniedPermissions list of denied permissions
         */
        void onResult(boolean allGranted, java.util.List<String> deniedPermissions);
    }

    private PermissionCallback callback;
    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            List<String> deniedList = new ArrayList<>();
            boolean allGranted = true;
            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                if (!entry.getValue()) {
                    deniedList.add(entry.getKey());
                    allGranted = false;
                }
            }
            if (callback != null) {
                callback.onResult(allGranted, deniedList);
            }
        });
    }

    public void requestNow(PermissionCallback callback, String... permissions) {
        this.callback = callback;
        List<String> toRequestList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                toRequestList.add(permission);
            }
        }
        if (toRequestList.isEmpty()) {
            // all permissions are granted
            callback.onResult(true, new ArrayList<>());
        } else {
            permissionLauncher.launch(toRequestList.toArray(new String[0]));
            this.callback = (allGranted, deniedList) -> {
                callback.onResult(deniedList.isEmpty(), deniedList);
            };
        }
    }
}
