package com.evanbarmes.Reminder;

import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class EnableButton extends Button {

    //the associated reminder
    Reminder reminder;
    //the associated reminder button
    Button button;

    public EnableButton (Button button) {

        this.reminder = ((ReminderButton) button).reminder;
        this.button = button;

        if (reminder.isEnabled()) {
            setText("√");
            addActionListener(new DisableReminder());
        }
        else {
            setText("");
            addActionListener(new EnableReminder());
        }

    }

    //push the enable button into delete mode
    public void enterDelete () {
        setText("X");
        getListeners().clear();
        addActionListener(new DeleteReminder());
    }

    //return the enable button to the enable/disable state
    public void exitDelete() {

        getListeners().clear();

        if (reminder.isEnabled()) {
            setText("√");
            addActionListener(new DisableReminder());
        }
        else {
            setText("");
            addActionListener(new EnableReminder());
        }

    }

    //when button is pressed in enabled mode
    class DisableReminder implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent evt){
            setText("");
            reminder.setEnabled(false);
            reminder.save();
            getListeners().clear();
            addActionListener(new EnableReminder());
        }
    }

    //when button is pressed in disabled mode
    class EnableReminder implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent evt){
            setText("√");
            reminder.setEnabled(true);
            reminder.save();
            getListeners().clear();
            addActionListener(new DisableReminder());
        }
    }

    //when button is pressed in delete mode
    class DeleteReminder implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent evt) {

            //grab the openIds, or create list if none exists
            ArrayList<String> openIds;
            if (!Storage.getInstance().exists("OpenIds")) openIds = new ArrayList<String>();
            else openIds = (ArrayList<String>) Storage.getInstance().readObject("OpenIds");

            //add the associated reminders id
            openIds.add(reminder.getId());

            //save the affected items
            Storage.getInstance().writeObject("OpenIds", openIds);
            Storage.getInstance().deleteStorageFile(reminder.getId());

            //remove this button and the associated reminder button
            getComponentForm().removeComponent(button);
            getComponentForm().removeComponent(evt.getComponent());

        }
    }

}
