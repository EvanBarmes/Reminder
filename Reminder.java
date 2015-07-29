package com.evanbarmes.Reminder;

import com.codename1.io.Externalizable;
import com.codename1.io.Storage;
import com.codename1.io.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Reminder implements Externalizable {


    private String name;
    public String getName() {return name;}

    boolean enabled = true;
    public boolean isEnabled() {return enabled;}
    public void setEnabled(boolean enabled) {this.enabled = enabled;}

    private String id;
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    //days the reminder should occur on
    private boolean[] repeating;
    public boolean[] getRepeating() {return repeating;}

    int hour;
    int minute;
    //AM or PM
    boolean period;


    public Reminder () {}

    //can't pass parameters to constructor due to externalizable
    public void setVars (String name, int hour, int minute, boolean period, boolean[] repeating) {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
        this.period = period;
        this.repeating = repeating;
    }

    //construct a readable string from the time data
    public String toString() {
        if (period) return String.valueOf(hour) + ":" + String.format("%02d", minute) + " PM";
        else return String.valueOf(hour) + ":" + String.format("%02d", minute) + " AM";
    }

    //get the next time in milliseconds that this reminder should trigger
    public long getTime() {

        if (!enabled) return 0;
        if (!repeating[0] && !repeating[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)]) return 0;

        if (period) return hour*1000*60*60+minute*1000*60+1000*60*60*12;
        else return hour*1000*60*60+minute*1000*60;

    }

    //save this reminder
    public void save() {
        Storage.getInstance().writeObject(id, this);
    }

    @Override
    public int getVersion() {
        return 1;
    }

    //save the data
    @Override
    public void externalize(DataOutputStream out) throws IOException {
        Util.writeUTF(name, out);
        Util.writeUTF(id, out);
        out.writeInt(hour);
        out.writeInt(minute);
        out.writeBoolean(period);
        for (boolean b : repeating) {
            out.writeBoolean(b);
        }
        out.writeBoolean(enabled);
    }

    //retrieve the data
    @Override
    public void internalize(int version, DataInputStream in) throws IOException {
        name = Util.readUTF(in);
        id = Util.readUTF(in);
        hour = in.readInt();
        minute = in.readInt();
        period = in.readBoolean();
        repeating = new boolean[]{in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean()};
        enabled = in.readBoolean();
    }

    @Override
    public String getObjectId() {
        return "Reminder";
    }

}
