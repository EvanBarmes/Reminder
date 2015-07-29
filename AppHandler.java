package com.evanbarmes.Reminder;

import com.codename1.io.Storage;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;
import java.util.*;
import java.util.Calendar;

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

        //set a timer to run every minute to check if a reminder needs to be pushed
        Timer timer = new Timer();
        TimerTask onTheMinute = new ReminderCheck();

        timer.schedule(onTheMinute, 60000 - java.util.Calendar.getInstance().getTimeInMillis() % 60000, 60000);



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

    //runs every minute on the minute
    public class ReminderCheck extends TimerTask {

        public void run()
        {
            //loop through the saved items
            for (String id : Storage.getInstance().listEntries()) {

                //if the item is a reminder
                if (id.length() > 7 && id.substring(0, 8).equals("Reminder")) {

                    //get the reminder
                    Reminder reminder = (Reminder) Storage.getInstance().readObject(id);

                    long millis = Calendar.getInstance().getTimeInMillis();

                    //check if it is time to push a reminder
                    long num1 = ((long) ((Calendar.getInstance().getTimeInMillis() + TimeZone.getDefault().getRawOffset() + 3600000)/1000.0 +.5) * 1000) % (1000*60*60*24);
                    long num2 = reminder.getTime();

                    if (num1 == num2) {

                        //create a new dialog
                        Dialog dialog = new Dialog("Don't Forget!");
                        dialog.setLayout(new GridLayout(3, 1));

                        //add the reminder name
                        dialog.addComponent(new Label(reminder.getName()));

                        //create a button to exit the dialog
                        Button button = new Button("Ok, thanks!");

                        //make the button close the dialog when clicked
                        button.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                dialog.dispose();
                            }
                        });

                        //add the button to the dialog
                        dialog.addComponent(button);

                        dialog.show();

                    }

                }

            }

        }

    }

}
