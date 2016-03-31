package ua.nure.vkmessanger;

import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Custom application class, which used to initialize VK SDK.
 */
public class Application extends android.app.Application {

    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (oldToken != null)
                Log.d("oldAccessToken", oldToken.accessToken);
            Log.d("newAccessToken", newToken.accessToken);
            AccessTokenManager.setAccessToken(getApplicationContext(), newToken.accessToken);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}
