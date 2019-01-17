package vinacert.vn.psample.printer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;


import java.io.File;
import java.io.FileInputStream;

import vinacert.vn.psample.Line;
import vinacert.vn.psample.MainApp;
import vinacert.vn.psample.Tool;


public class PrintItemImage extends PrintItem{
    private String file_name;
    protected int width =40;
    protected int height =40;
    public PrintItemImage(Line line)
    {
        super(line);
        file_name = line.getValue();
        if(!"".equals(line.getAttribute("scale")))
        {
            this.scale = Double.parseDouble(line.getAttribute("scale"));
        }
        if(!"".equals(line.getAttribute("w")))
        {
            width = Integer.parseInt(line.getAttribute("w"));

        }
        if(!"".equals(line.getAttribute("h")))
        {
            height = Integer.parseInt(line.getAttribute("h"));

        }
    }
    protected double scale =1;
    @Override
    public int getHeight() {
        return (int) (height * scale);
    }
    @Override
    public int draw(Canvas g, int x, int y, int width) {


        //AffineTransform oldt = g.getTransform();

        //g.translate(x, y  );
        //g.scale(scale, scale);

        try {
            File f = new File(file_name);
            if(f.exists())
            {
                FileInputStream fis = null;
                fis = new FileInputStream(file_name);
                Bitmap image = BitmapFactory.decodeStream(fis);
                fis.close();
                fis = null;
                if(image.getWidth()>width || image.getHeight()>height)
                {
                    //Resize image
                    image = Tool.getInstance().getResizedBitmap(image, width, height);
                }
                g.drawBitmap(image, x, y, getPaint());

            }
            y = height;


        }
        catch (Exception e) {
            //g.drawRect(0, 0, width, height, getPaint());
           // g.drawLine(0, 0, height, m_iSize, getPaint());
            //g.drawLine(height, 0, 0, m_iSize, getPaint());

        }
        return  y;
        //g2d.setTransform(oldt);
    }
}
