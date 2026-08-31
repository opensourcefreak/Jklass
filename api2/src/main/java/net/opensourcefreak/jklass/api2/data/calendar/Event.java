package net.opensourcefreak.jklass.api2.data.calendar;

import java.util.Date;

public class Event {
    public String title;
    public Date start;
    public Date end;
    public String location;
    public boolean cancelled;
    public boolean allDay;
    // I don't know whatever the fuck this is supposed to be but everything that is not eventType 1
    // is trash
    public int eventType;
}
