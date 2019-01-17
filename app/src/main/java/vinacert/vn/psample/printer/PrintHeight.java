package vinacert.vn.psample.printer;

import android.graphics.Canvas;

import vinacert.vn.psample.Line;

public class PrintHeight extends PrintItem {
    public PrintHeight(Line line)
    {
        super(line);

    }
    private int height = 0;
    public int getHeight()
    {
        String s = getLine().getValue();
        try
        {
            height = Integer.parseInt(s);
        }catch (Exception ex)
        {
            System.out.println(ex.toString());
        }

        return height;
    }
    public int draw(Canvas g, int x, int y, int width)
    {
        return  height;
    }
}
