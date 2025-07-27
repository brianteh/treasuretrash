package com.twj.yourtrashmytreasure.auth;

import com.twj.yourtrashmytreasure.model.User;

public interface AuthListener {

    void OnAuthentication(User user);
}
