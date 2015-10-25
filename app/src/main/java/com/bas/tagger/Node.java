package com.bas.tagger;

/**
 * Created by willo on 25/10/2015.
 */
public class Node {

    public int icon;
    public String nodeid;
    public String[] messages;

    public Node()
    {
        super();
    }

    public Node(int _icon,String _nodeid, String[] _messages)
    {
        this.icon = _icon;
        this.nodeid = _nodeid;
        this.messages = _messages;
    }
}
