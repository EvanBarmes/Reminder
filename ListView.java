package com.evanbarmes.Reminder;

import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.Button;
import com.codename1.ui.Form;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.table.TableLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListView implements View {

    //the form component
    private Form form;
    public Form getForm() {return form;}

    //ref to the handler
    private AppHandler handler;
    //ref to the enableButtons
    private List<EnableButton> buttons;

    public ListView(AppHandler handler) {

        //Register the reminder class for externalization
        Util.register("Reminder", Reminder.class);

        this.handler = handler;
        form = new Form("Current Reminders");

        //set the form to slide in/out from left/right
        form.setTransitionInAnimator(CommonTransitions.createSlide(0, true, 500));
        form.setTransitionOutAnimator(CommonTransitions.createSlide(0, false, 500));

        //set up a new table layout
        TableLayout tableLayout = new TableLayout(Storage.getInstance().listEntries().length+1, 2);
        TableLayout.Constraint constraint;
        form.setLayout(tableLayout);

        //create a list of enableButtons and pairs of enableButtons/reminderButtons
        buttons = new ArrayList<EnableButton>();
        ArrayList<ButtonPair> pairs = new ArrayList<ButtonPair>();

        //loop through the saved items
        for (String id : Storage.getInstance().listEntries()) {

            //if the item is a reminder
            if (id.length() > 7 && id.substring(0, 8).equals("Reminder")) {

                //create a new reminderButton and give it a ref to a reminder
                ReminderButton button = new ReminderButton((Reminder) Storage.getInstance().readObject(id), handler);

                //create a new enableButton and give it a ref to its reminderButton
                EnableButton enableButton = new EnableButton(button);

                //add the buttons to their lists
                pairs.add(new ButtonPair(button.reminder.getName(), enableButton));
                buttons.add(enableButton);

                System.out.println(id + " and " + button.reminder.getName());
            } else System.out.println("Hidden: " + id);

        }

        //sort the buttons alphabetically
        Collections.sort(pairs);

        //loop through the buttonPairs
        for (ButtonPair pair : pairs) {

            //add the enableButton to the form
            constraint = tableLayout.createConstraint();
            constraint.setWidthPercentage(15);
            form.addComponent(constraint, pair.button);

            //add the reminderButton to the form
            constraint = tableLayout.createConstraint();
            constraint.setWidthPercentage(85);
            form.addComponent(constraint, ((EnableButton) pair.button).button);

        }

        System.out.println("    End    ");
        System.out.println();

        //create editButton
        Button editButton = new Button("O");
        editButton.addActionListener(new EnterEdit());

        //add the edit button to the form
        constraint = tableLayout.createConstraint();
        constraint.setWidthPercentage(25);
        form.addComponent(constraint, editButton);

        //create the newReminderButton
        Button enter = new Button("Create New Reminder");
        enter.addActionListener(new NewReminder());

        //add the newReminderButton to the form
        constraint = tableLayout.createConstraint();
        constraint.setWidthPercentage(75);
        form.addComponent(constraint, enter);

    }

    //when new reminder button is clicked
    class NewReminder implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent evt){
            //create a new view and pass the form to the handler for displaying
            handler.newForm(new ReminderView(handler).getForm());
        }
    }

    //when edit button is pressed
    class EnterEdit implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent evt){
            //push all of the enable buttons into delete mode
            for (EnableButton button : buttons) {
                button.enterDelete();
                Button editButton = (Button) evt.getComponent();
                editButton.getListeners().clear();
                editButton.addActionListener(new ExitEdit());
            }
        }
    }

    //when the edit button is repressed
    class ExitEdit implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent evt){
            //return all enable buttons back to their enable/disable state
            for (EnableButton button : buttons) {
                button.exitDelete();
                Button editButton = (Button) evt.getComponent();
                editButton.getListeners().clear();
                editButton.addActionListener(new EnterEdit());
            }
        }
    }

}
