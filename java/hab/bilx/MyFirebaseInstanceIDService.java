package hab.bilx;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * This class enables developers to create a token for the notifications they can send to all account types
 * @author Hanzallah Burney
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String REG_TOKEN = "REG_TOKEN";

    @Override
    public void onTokenRefresh(){
        // Log the current id token for notification
        String recent_Token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN,recent_Token);
    }
}
/*
 *  @author Hanzallah Burney
 */