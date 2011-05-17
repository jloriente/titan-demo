package com.moremr.teknikforetagen.data;

import org.mozilla.javascript.Scriptable;

public interface NewsMVC {

    /** Initialize the model. Must be called before accessing other methods. */
    public void init();

    /** Get the full list of news items. Returns a JS array of items. */
    public Scriptable getItems();

    /** Set the currently selected list item. Returns the selected news item data. */
    public Scriptable setCurrentItem(int idx);

    /** Get the currently selected news item. */
    public Scriptable getCurrentItem();

    /** Get the number of unread news items. */
    public int getUnreadCount();

    /** Test whether a news item has been previously read. */
    public boolean isRead(Scriptable item);

}
