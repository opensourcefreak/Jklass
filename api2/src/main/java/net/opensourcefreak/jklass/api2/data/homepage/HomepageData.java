package net.opensourcefreak.jklass.api2.data.homepage;

import java.util.ArrayList;

public class HomepageData {
    public String coverImage;
    public String firstName;

    // Only sometimes present in the response, but i think this is the type
    // Nevermind, it's an event type that we have not yet reverse engineered
    //public Event nowEvent;
    //public Event nextEvent;

    public ArrayList<NewsItem> newsItems;
}
