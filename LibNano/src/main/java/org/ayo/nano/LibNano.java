package org.ayo.nano;

import android.app.Application;

public class LibNano {

    public static Application app;

    public static void setApplication(Application app) {
        LibNano.app = app;
    }
}
