package vinacert.vn.psample;

public class LineParser {
    private Line line;
    private java.util.ArrayList<Line> lines;
    public LineParser()
    {
        line = new Line();
        lines = new java.util.ArrayList<>();

    }
    private String text = "";
    private void startElement(String qName, String attributes)
    {
        text = "";
        Line current = new Line();
        current.setName(qName);
        if(!"".equals(attributes))
        {
            while(true)
            {
                int index = attributes.indexOf('=');
                if(index == -1)
                {
                    break;
                }
                String key = attributes.substring(0, index).trim();
                attributes = attributes.substring(index + 1);
                index = attributes.indexOf('\"');
                if(index != -1)
                {
                    attributes = attributes.substring(index + 1);
                    index = attributes.indexOf('\"');
                    if(index != -1)
                    {
                        String value = attributes.substring(0, index);
                        attributes = attributes.substring(index + 1);
                        current.addAttribute(key, value);
                    }
                }
            }
        }

        lines.add(current);

    }
    private void endElement(String qName)
    {
        if(lines.size()>0)
        {
            Line c = lines.get(lines.size()-1);
            c.setValue(text);

            lines.remove(lines.size()-1);

            if(lines.size()>0)
            {
                lines.get(lines.size() -1).add(c);
            }else
            {
                line.add(c);
            }
        }
        text = "";
    }
    public void printLines(Line p, String space)
    {
        for(int i =0; i<p.size(); i++)
        {
            Line c = p.get(i);
            String attributes = "";

            for(int j=0; j<c.attSize(); j++)
            {
                attributes += " ";

                attributes = attributes + c.getAttributeKey(j) + "=\"" + c.getAttributeValue(j) + "\"";
            }
            String s = space + "<" + c.getName() + "" + attributes + ">" + c.getValue() + "</" + c.getName() + ">";
            System.out.println(s);
            printLines(c, space + "\t");

        }
    }
    public Line parse(String strInLine)
    {
        int posF = 0;
        int posL = 0;
        int posS = 0;

        String strTagName;
        while(true)
        {
            posF = strInLine.indexOf("<");
            if(posF != -1)
            {
                text= text + strInLine.substring(0, posF);
                strInLine = strInLine.substring(posF);
                posF = 0;

            }
            posL = strInLine.indexOf(">");
            if(posF == -1 && posL == -1)
            {
                break;
            }
            if(posF == -1 || posL == -1)
            {
                if(posF != -1)
                {
                    text = text + strInLine.substring(0, posF + 1);
                    strInLine = strInLine.substring(posF + 1);
                }
                if(posL != -1)
                {
                    text = text + strInLine.substring(0, posL + 1);
                    strInLine = strInLine.substring(posL + 1);
                }
                continue;
            }
            posS = strInLine.indexOf(' ');

            if (posS < posL && posS > posF) {
                strTagName = strInLine.substring(posF + 1, posS - posF);
            }
            else {
                strTagName = strInLine.substring(posF + 1, posL - posF );
            }
            if (!"".equals(strTagName) && "/".equals(strTagName.substring(0, 1))) {
                strTagName = strTagName.substring(1);
                endElement(strTagName);
            }else
            {
                String att ="";
                if(posS != -1 && posS < posL)
                {

                    att = strInLine.substring(posS, posL);

                }
                startElement(strTagName, att);

            }
            strInLine = strInLine.substring(posL + 1);
        }

        return line;
    }

}
