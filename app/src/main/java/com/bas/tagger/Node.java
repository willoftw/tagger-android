package com.bas.tagger;

/**
 * Created by willo on 25/10/2015.
 */
public class Node {

    public int icon;
    public String uuid,nodeid;
    public int major;
    public int minor;
    public String messages = "";
    public String uses = "";

    public Node()
    {
        super();
    }

    public Node(int _icon,String _uuid, int _major, int _minor)
    {
        this.icon = _icon;
        this.uuid = _uuid;
        this.nodeid = _uuid.substring(0, _uuid.length() - 10);;
        this.major = _major;
        this.minor = _minor;
    }


}
