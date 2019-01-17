package vinacert.vn.psample.printer;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;

import vinacert.vn.psample.Line;

public class PrintItemText extends PrintItem {


    public PrintItemText(Line line)
    {
        super(line);

    }


    private ArrayList<String> ss;
    int fontsize = 10;
    @Override
    public int getHeight()
    {
        String fontname = "Arial";

        int style = 0;
        if(!"".equals(getLine().getAttribute("font-name")))
        {
            fontname = getLine().getAttribute("font-name");
        }
        if(!"".equals(getLine().getAttribute("font-size")))
        {
            fontsize = Integer.parseInt(getLine().getAttribute("font-size"));
        }
        getPaint().setTextSize(fontsize);
        if(null != getLine().getAttribute("font-style"))
            switch (getLine().getAttribute("font-style")) {
                case "bold":
                    Typeface currentTypeFace =   getPaint().getTypeface();
                    Typeface bold = Typeface.create(currentTypeFace, Typeface.BOLD);
                    getPaint().setTypeface(bold);
                    break;
                case "italic":
                    currentTypeFace =   getPaint().getTypeface();
                    Typeface italic = Typeface.create(currentTypeFace, Typeface.ITALIC);
                    getPaint().setTypeface(italic);
                    break;
                case "bold-italic":
                    currentTypeFace =   getPaint().getTypeface();
                    Typeface bi = Typeface.create(currentTypeFace, Typeface.BOLD_ITALIC);
                    getPaint().setTypeface(bi);
                    break;
                case "strikeout":

                    break;
                default:
                    break;
            }
        ss = new ArrayList();
        String s = getLine().getValue();
        if(s.contains("Địa"))
        {
            int a = 1;
        }
        String store = "";
        while(true) {
            Rect result = new Rect();
            getPaint().getTextBounds(s, 0, s.length(), result);
            int stringLen = result.width();
            if (stringLen < getWidth()) {
                ss.add(s.trim());
                if (!store.equals("")) {
                    s = store;
                    store = "";
                } else {
                    break;
                }
            } else {
                int index = s.lastIndexOf(' ');
                if (index == -1) {
                    ss.add(s);
                    break;
                } else {
                    store = s.substring(index) + store;
                    s = s.substring(0, index);
                }
            }
        }
        return fontsize * ss.size();
    }


    @Override
    public int draw(Canvas g, int x, int y, int width)
    {

        getPaint().setTextSize(fontsize);
        for(String s:ss)
        {

            if(getLine().getAttribute("align").equals("center"))
            {
                Rect result = new Rect();

                getPaint().getTextBounds(s, 0, s.length(), result);
                int stringLen = result.width();
                if(stringLen<width)
                {
                    width = width - stringLen;
                    x= x + (width/2);
                }
            }
            else if(getLine().getAttribute("align").equals("right"))
            {
                //int stringLen = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
                Rect result = new Rect();
                getPaint().getTextBounds(s, 0, s.length(), result);
                int stringLen = result.width();
                if(stringLen<width)
                {
                    width = width - stringLen;
                    x= x + width;
                }
            }
            float baseline = -getPaint().ascent(); // ascent() is negative

            int height = (int) (baseline + getPaint().descent() + 0.5f);
            g.drawText(s, x, y + baseline, getPaint() );
            y += height;
        }
        return y;
    }
}
