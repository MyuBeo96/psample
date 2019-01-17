package vinacert.vn.psample.printer;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.google.zxing.EncodeHintType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import vinacert.vn.psample.Line;

public class PrintItemQrcode extends PrintItem {
    protected String m_sCode;
    protected int m_iSize =40;

    protected double scale =1;

    public PrintItemQrcode(Line line)
    {
        super(line);
        m_sCode = line.getValue();
        if(!"".equals(line.getAttribute("scale")))
        {
            this.scale = Double.parseDouble(line.getAttribute("scale"));
        }
        if(!"".equals(line.getAttribute("size")))
        {
            m_iSize = Integer.parseInt(line.getAttribute("size"));

        }
    }
    @Override
    public int getHeight() {
        return (int) (m_iSize * scale);
    }
    @Override
    public int draw(Canvas g, int x, int y, int width) {


        //AffineTransform oldt = g.getTransform();

        //g.translate(x, y  );
        //g.scale(scale, scale);

        try {
            Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
            Charset utf8 = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                utf8 = StandardCharsets.UTF_8;
            }
            hintMap.put(EncodeHintType.CHARACTER_SET, utf8.displayName());
            hintMap.put(EncodeHintType.MARGIN, 0);
            //hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            //QRCodeWriter qrCodeWriter = new QRCodeWriter();
            //BitMatrix byteMatrix = qrCodeWriter.encode(m_sCode, BarcodeFormat.QR_CODE, m_iSize, m_iSize, hintMap);

            QRGEncoder qrgEncoder = new QRGEncoder(m_sCode, null, QRGContents.Type.TEXT, m_iSize);
            Bitmap image = qrgEncoder.encodeAsBitmap();
            g.drawBitmap(image, x, y, getPaint());
            y += image.getHeight();

        }
        catch (Exception e) {
            g.drawRect(0, 0, m_iSize, m_iSize, getPaint());
            g.drawLine(0, 0, m_iSize, m_iSize, getPaint());
            g.drawLine(m_iSize, 0, 0, m_iSize, getPaint());

        }
        return  y;
        //g2d.setTransform(oldt);
    }
}
