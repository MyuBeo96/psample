package vinacert.vn.psample.printer;

import android.graphics.Canvas;
import android.graphics.Paint;

import vinacert.vn.psample.Line;

public class PrintItem {
    private Line line;
    private Paint paint;
    public Paint getPaint()
    {
        if(paint == null)
        {
            paint = new Paint();

        }
        return paint;
    }
    public PrintItem(Line line)
    {
        this.line = line;
    }
    public Line getLine()
    {
        return line;
    }
    public int getHeight()
    {
        return 0;
    }
    private int w = 0;
    public int getWidth()
    {
        return w;
    }
    public void setWidth(int w)
    {
        this.w = w;
    }
    public int draw(Canvas g, int x, int y, int width)
    {
        return  0;
    }
}
