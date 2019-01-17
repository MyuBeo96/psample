package vinacert.vn.psample.printer;

import android.graphics.Canvas;

import vinacert.vn.psample.Line;

public class PrintItemLine extends PrintItem {
    public PrintItemLine(Line l)
    {
        super(l);
    }

    @Override
    public int draw(Canvas g, int x, int y, int width)
    {
        return 0;
    }
}