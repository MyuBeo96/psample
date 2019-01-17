package vinacert.vn.psample;

public class Line {

    private final java.util.ArrayList<Line> lines;
    private final java.util.ArrayList<KeyValue> attributes;
    public Line()
    {
        lines = new java.util.ArrayList<>();
        attributes = new java.util.ArrayList<>();
    }

    private String id = "";
    private String name = "";
    private String value = "";
    private Line parent = null;
    public void add(Line c)
    {
        lines.add(c);
    }
    public Line get(int i)
    {
        return lines.get(i);
    }
    public void clear()
    {
        lines.clear();
    }
    public void addAttribute(String key, String value)
    {
        for(KeyValue k : attributes)
        {
            if(k.key.equals(key))
            {
                k.value = value;
                return;
            }
        }

        KeyValue k = new KeyValue();
        k.key = key;
        k.value = value;
        attributes.add(k);
    }

    public String getAttribute(String key)
    {
        for(KeyValue k : attributes)
        {
            if(k.key.equals(key))
            {
                return k.value;
            }
        }
        return "";
    }
    public String getAttributeValue(int i)
    {
        return attributes.get(i).value;
    }
    public String getAttributeKey(int i)
    {
        return attributes.get(i).key;
    }

    public int attSize()
    {
        return attributes.size();
    }
    public int size()
    {
        return lines.size();
    }
    public Line find(String id)
    {
        if(this.id.equals(id))
        {
            return this;
        }
        for(int i=0; i<size(); i++)
        {
            Line found = lines.get(i).find(id);
            if(found != null)
            {
                return found;
            }
        }
        return null;
    }
    public Line findByName(String _name)
    {
        if(name.equals(_name))
        {
            return this;
        }
        for(int i=0; i<size(); i++)
        {
            Line found = lines.get(i).findByName(name);
            if(found != null)
            {
                return found;
            }
        }
        return null;
    }
    public String findValueByName(String name)
    {
        Line found = findByName(name);
        if(found != null)
        {
            return found.getValue();
        }
        return "";
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the parent
     */
    public Line getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Line parent) {
        this.parent = parent;
    }
    private void travelLine(java.util.ArrayList<Line> lines, Line l, String name)
    {
        for(int k =0; k<l.size(); k++)
        {
            Line c = l.get(k);

            if(name.equals(c.getName()))
            {
                lines.add(c);
            }
            else
            {
                travelLine(lines, c, name);
            }
        }
    }
    public java.util.ArrayList<Line> listNames(String name)
    {
        java.util.ArrayList<Line> lines = new java.util.ArrayList<>();
        travelLine(lines, this, name);
        return lines;
    }

}