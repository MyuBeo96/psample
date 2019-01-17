package vinacert.vn.psample.printer;

import android.graphics.Bitmap;
import android.graphics.Canvas;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import vinacert.vn.psample.Line;


public class PrintItemBarcode extends PrintItem{

    protected String bCode;
    protected int height = 60;
    protected int width = 60;
    private String type = "";
    protected double scale =1;

    public PrintItemBarcode(Line line)
    {
        super(line);
        bCode = line.getValue();
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
        type = line.getAttribute("type");


    }

    @Override
    public int getHeight() {
        return (int) (height * scale);
    }
    @Override
    public int draw(Canvas g, int x, int y, int width) {

        try {

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = null;
            if(type == "code128")
            {
                bitMatrix  = multiFormatWriter.encode(bCode, BarcodeFormat.CODE_128,width,height);
            }else if(type == "ean8")
            {
                bitMatrix  =  multiFormatWriter.encode(bCode, BarcodeFormat.EAN_8,width,height);
            }else if(type == "ean13")
            {
                bitMatrix  =  multiFormatWriter.encode(bCode, BarcodeFormat.EAN_13,width,height);
            }else if(type == "code93")
            {
                bitMatrix  =  multiFormatWriter.encode(bCode, BarcodeFormat.CODE_93,width,height);
            }else if(type == "qrcode")
            {
                bitMatrix  =  multiFormatWriter.encode(bCode, BarcodeFormat.QR_CODE,width,height);
            }
            else
            {
                 bitMatrix  = multiFormatWriter.encode(bCode, BarcodeFormat.CODE_39,width,height);
            }

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            Bitmap image = barcodeEncoder.createBitmap(bitMatrix);
            g.drawBitmap(image, x, y, getPaint());
            y += image.getHeight();

        }
        catch (Exception e) {
            String s = e.toString();
            //g.drawRect(0, 0, bSize, bSize, getPaint());
            //g.drawLine(0, 0, bSize, bSize, getPaint());
           // g.drawLine(bSize, 0, 0, bSize, getPaint());

        }
        return  y;
        //g2d.setTransform(oldt);
    }



}
