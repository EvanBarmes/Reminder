package com.evanbarmes.Reminder;

import com.codename1.io.Storage;
import com.codename1.ui.*;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;

public class AppHandler {

    //the current form to show
    private Form current;

    public void init(Object context) {

        try{

            //set the theme on init
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));

        } catch(IOException e){

            e.printStackTrace();

        }

    }

    public void start() {

        //to clear saved data for testing purposes
        //Storage.getInstance().clearStorage();

        //if there's a currrent form show it
        if(current != null){

            current.show();
            return;

        }

        //create a new form if none exist
        newForm((new ListView(this)).getForm());

    }

    public void newForm(Form form) {

        //apply the new form
        current = form;
        current.show();

    }

    public void stop() {

        current = Display.getInstance().getCurrent();

    }

    public void destroy() {

    }
}
