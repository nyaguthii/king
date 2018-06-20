package org.gilbre.app.gilbre;

import android.content.SharedPreferences;

/**
 * Created by root on 12/8/17.
 */

public class TokenManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static TokenManager INSTANCE=null;

    private TokenManager(SharedPreferences prefs){
        this.prefs=prefs;
        this.editor=prefs.edit();

    }

    static synchronized TokenManager getINSTANCE(SharedPreferences prefs){

        if(INSTANCE ==null){
            INSTANCE = new TokenManager(prefs);
        }

        return INSTANCE;
    }

    public void saveToken(AccessToken token){
        editor.putString("ACCESS_TOKEN",token.getAccess_token()).commit();
        //editor.putString("TOKEN_TYPE",token.getToken_type()).commit();
        editor.putString("REFRESH_TOKEN",token.getRefresh_token()).commit();
        //editor.putInt("REFRESH_IN",token.getExpires_in()).commit();

    }

    public void deleteToken(){
        editor.remove("ACCESS_TOKEN").commit();
        editor.remove("REFRESH_TOKEN").commit();
    }
    public AccessToken getToken(){
        AccessToken token = new AccessToken();
        token.setAccess_token(prefs.getString("ACCESS_TOKEN",null));
        token.setRefresh_token(prefs.getString("REFRESH_TOKEN",null));

        return token;
    }
}
