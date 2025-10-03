package com.tools.easytools.wartdipara.EasyPermission;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class PermissionInstance {
    private final static String TAG = "PERMISSION_FRAGMENT_TAG";

    private PermissionInstance() {
    }

    private static final class InstanceHolder {
        private static final PermissionInstance instance = new PermissionInstance();
    }

    public static PermissionInstance getInstance() {
        return InstanceHolder.instance;
    }

    public void request(FragmentActivity activity, SupportFragment.PermissionCallback callback, String... permissions) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment existedFragment = fragmentManager.findFragmentByTag(TAG);
        SupportFragment fragment;
        if (existedFragment != null) {
            fragment = (SupportFragment) existedFragment;
        } else {
            fragment = new SupportFragment();
            fragmentManager.beginTransaction().add(fragment, TAG).commitNow();
        }
        fragment.requestNow(callback, permissions);
    }

    public void request(Fragment fragment, SupportFragment.PermissionCallback callback, String... permissions) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        Fragment existedFragment = fragmentManager.findFragmentByTag(TAG);
        SupportFragment supportFragment;
        if (existedFragment != null) {
            supportFragment = (SupportFragment) existedFragment;
        } else {
            supportFragment = new SupportFragment();
            fragmentManager.beginTransaction().add(supportFragment, TAG).commitNow();
        }
        supportFragment.requestNow(callback, permissions);
    }

}
