package ua.nure.vkmessanger;

import com.vk.sdk.VKSdk;

/**
 * Custom application class, which used to initialize VK SDK.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
