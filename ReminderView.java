package com.evanbarmes.Reminder;

import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Form;
import com.codename1.ui.TextField;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.spinner.TimeSpinner;

import java.util.ArrayList;

public class ReminderView implements View {

    private Form form;
    public Form getForm() {return form;}

    public AppHandler handler;

    TimeSpinner picker;

    //button to save the reminder and return to the list view
    Button setReminder;

    //enter the name of the reminder
    TextField textField;

    //the repeating checkboxes
    CheckBox checkBox0;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;
    CheckBox checkBox6;
    CheckBox checkBox7;

    public ReminderView (AppHandler handler) {

        this.handler = handler;

        //create a new form and name it
        form = new Form("Select Alarm Time");

        //no transitions/ in and out are on the list view
        form.setTransitionInAnimator(CommonTransitions.createEmpty());
        form.setTransitionOutAnimator(CommonTransitions.createEmpty());

        //create a box layout to use for the reminder view
        form.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        //create the elements
        textField = new TextField("Name");
        form.addComponent(textField);

        picker = new TimeSpinner();
        form.addComponent(picker);

        checkBox0 = new CheckBox("Just Once");
        checkBox0.setSelected(true);

        checkBox1 = new CheckBox("Every Sunday");
        checkBox2 = new CheckBox("Every Monday");
        checkBox3 = new CheckBox("Every Tuesday");
        checkBox4 = new CheckBox("Every Wednesday");
        checkBox5 = new CheckBox("Every Thursday");
        checkBox6 = new CheckBox("Every Friday");
        checkBox7 = new CheckBox("Every Saturday");

        //if justOnce is selected then unselect all other days
        checkBox0.addActionListener(new JustOnce(new CheckBox[]{checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7}));

        //untick justOnce when another box is ticked
        ActionListener untick = new Repeating(checkBox0);
        checkBox1.addActionListener(untick);
        checkBox2.addActionListener(untick);
        checkBox3.addActionListener(untick);
        checkBox4.addActionListener(untick);
        checkBox5.addActionListener(untick);
        checkBox6.addActionListener(untick);
        checkBox7.addActionListener(untick);

        form.addComponent(checkBox0);
        form.addComponent(checkBox1);
        form.addComponent(checkBox2);
        form.addComponent(checkBox3);
        form.addComponent(checkBox4);
        form.addComponent(checkBox5);
        form.addComponent(checkBox6);
        form.addComponent(checkBox7);

        //set up the button to create the reminder
        setReminder = new Button("Set Reminder");
        setReminder.addActionListener(new SaveReminder());
        form.addComponent(setReminder);

        //create a cancel button
        Button cancel = new Button("Cancel");
        cancel.addActionListener(new Cancel());
        form.addComponent(cancel);

    }

    //when reexamining a reminder, this is to set the component values to what is saved
    public void updating (Reminder reminder) {
        textField.setText(reminder.getName());
        picker.setCurrentHour(reminder.hour);
        picker.setCurrentMinute(reminder.minute);
        picker.setCurrentMeridiem(reminder.period);
        boolean[] repeating = reminder.getRepeating();
        checkBox0.setSelected(repeating[0]);
        checkBox1.setSelected(repeating[1]);
        checkBox2.setSelected(repeating[2]);
        checkBox3.setSelected(repeating[3]);
        checkBox4.setSelected(repeating[4]);
        checkBox5.setSelected(repeating[5]);
        checkBox6.setSelected(repeating[6]);
        checkBox7.setSelected(repeating[7]);
        setReminder.setText("Update Reminder");
        setReminder.getListeners().clear();
        setReminder.addActionListener(new SaveReminder(reminder));
    }

    //when save reminder button is pressed
    class SaveReminder implements ActionListener {

        //store a ref to the reminder
        Reminder reminder;

        public SaveReminder(Reminder reminder) {
            this.reminder = reminder;
        }

        public SaveReminder() {
        }

        //when the button is pressed
        @Override
        public void actionPerformed(ActionEvent evt) {

            if (reminder == null) {

                reminder = new Reminder();

                //give the reminder an openId
                if (Storage.getInstance().exists("OpenIds") && ((ArrayList<String>) Storage.getInstance().readObject("OpenIds")).size() > 0) {
                    ArrayList<String> array = (ArrayList<String>) Storage.getInstance().readObject("OpenIds");
                    reminder.setId(array.remove(0));
                    Storage.getInstance().writeObject("OpenIds", array);
                } else reminder.setId("Reminder" + Storage.getInstance().listEntries().length);

            }

            boolean[] repeater = new boolean[]{checkBox0.isSelected(), checkBox1.isSelected(), checkBox2.isSelected(), checkBox3.isSelected(), checkBox4.isSelected(), checkBox5.isSelected(), checkBox6.isSelected(), checkBox7.isSelected()};

            //grab the current status of the components and save the reminder
            reminder.setVars(textField.getText(), picker.getCurrentHour(), picker.getCurrentMinute(), picker.isCurrentMeridiem(), repeater);
            Storage.getInstance().writeObject(reminder.getId(), reminder);

            //return to the list view
            handler.newForm((new ListView(handler).getForm()));

        }
    }

    //when one of the repeating days is ticked
    class Repeating implements ActionListener {

        CheckBox checkBox;

        public Repeating (CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        //untick the justOnce box
        @Override
        public void actionPerformed(ActionEvent evt) {
                checkBox.setSelected(false);
        }

    }

    //when justOnce is ticked
    class JustOnce implements ActionListener {

        CheckBox[] boxes;

        public JustOnce (CheckBox[] boxes) {
            this.boxes = boxes;
        }

        //untick all of the boxes
        @Override
        public void actionPerformed (ActionEvent evt){
            for(CheckBox box : boxes) {
                box.setSelected(false);
            }
        }

    }

    //when the cancel button is pressed
    class Cancel implements ActionListener {

        //just return to the list view without saving
        @Override
        public void actionPerformed (ActionEvent evt){
            //return to the list view
            handler.newForm((new ListView(handler).getForm()));
        }

    }

}
