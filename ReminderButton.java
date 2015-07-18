package com.evanbarmes.Reminder;

import com.codename1.ui.Button;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

public class ReminderButton extends Button {

    //the associated reminder
    Reminder reminder;

    AppHandler handler;

    //set the reminder name as the text displayed and add the action listener
    public ReminderButton (Reminder reminder, AppHandler handler) {
        this.handler = handler;
        this.reminder = reminder;
        this.setText(reminder.getName());
        addActionListener(new ViewReminder());
    }

    //when the button is pressed
    class ViewReminder implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent evt){
            //examine the details of the reminder
            ReminderView view = new ReminderView(handler);
            view.updating(reminder);
            handler.newForm(view.getForm());
        }
    }

}
