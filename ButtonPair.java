package com.evanbarmes.Reminder;

import com.codename1.ui.Button;

public class ButtonPair implements Comparable<ButtonPair> {

    //the name that is printed on the reminder button
    String name;
    //the enable button associated with the reminder button
    Button button;


    public ButtonPair(String name, Button button) {
        this.button = button;
        this.name = name;
    }

    //used to list the reminders alphabetically
    @Override
    public int compareTo(ButtonPair o) {
        return name.compareToIgnoreCase(o.name);
    }
}