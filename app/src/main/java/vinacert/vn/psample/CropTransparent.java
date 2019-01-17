package vinacert.vn.psample;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Arrays;

/**
 * Created by Álvaro Menezes on 9/9/16.
 */
public class CropTransparent {

    /***
     * Trim an image, removing transparent borders.
     * @param bitmap image to crop
     * @return square bitmap with the cropped image
     */
    public Bitmap crop (Bitmap bitmap){

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        int[] empty = new int[width];
        int[] buffer = new int[width];
        Arrays.fill(empty,0);

        int top = 0;
        int left = 0;
        int botton = height;
        int right = width;

        for (int y = 0; y < height; y++) {
            bitmap.getPixels(buffer, 0, width, 0, y, width, 1);
            if (!Arrays.equals(empty, buffer)) {
                top = y;
                break;
            }
        }

        for (int y = height - 1; y > top; y--) {
            bitmap.getPixels(buffer, 0, width, 0, y, width, 1);
            if (!Arrays.equals(empty, buffer)) {
                botton = y;
                break;
            }
        }


        int bufferSize = botton -top +1;
        empty = new int[bufferSize];
        buffer = new int[bufferSize];
        Arrays.fill(empty,0);

        for (int x = 0; x < width; x++) {
            bitmap.getPixels(buffer, 0, 1, x, top + 1, 1, bufferSize);
            if (!Arrays.equals(empty, buffer)) {
                left = x;
                break;
            }
        }

        Arrays.fill(empty, 0);
        for (int x = width - 1; x > left; x--) {
            bitmap.getPixels(buffer, 0, 1, x, top + 1, 1, bufferSize);
            if (!Arrays.equals(empty, buffer)) {
                right = x;
                break;
            }
        }

        Bitmap cropedBitmap = Bitmap.createBitmap(bitmap, left, top, right-left, botton-top);
        return cropedBitmap;
    }
    Bitmap CropBitmapTransparency(Bitmap sourceBitmap)
    {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for(int y = 0; y < sourceBitmap.getHeight(); y++)
        {
            for(int x = 0; x < sourceBitmap.getWidth(); x++)
            {
                int alpha = (sourceBitmap.getPixel(x, y) >> 24) & 255;
                if(alpha > 0)   // pixel is not 100% transparent
                {
                    if(x < minX)
                        minX = x;
                    if(x > maxX)
                        maxX = x;
                    if(y < minY)
                        minY = y;
                    if(y > maxY)
                        maxY = y;
                }
            }
        }
        if((maxX < minX) || (maxY < minY))
            return null; // Bitmap is entirely transparent

        // crop bitmap to non-transparent area and return:
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }
    public Bitmap cropTransparentArea(Bitmap bitmap2) {
        int minX = bitmap2.getWidth(), maxX = 0, minY = bitmap2.getHeight(), maxY = 0;
        // finding last start and end position for solid non transparent area.
        int color;
        for (int x = 0; x < bitmap2.getWidth(); x++) {

            for (int y = 0; y < bitmap2.getHeight(); y++) {
                color = bitmap2.getPixel(x, y);
                if ( color != Color.WHITE) {
                    if (minY > y) {
                        minY = y;

                    }
                    if (maxY < y) {
                        maxY = y;

                    }
                    if (minX > x) {
                        minX = x;

                    }
                    if (maxX < x) {
                        maxX = x;

                    }
                }

            }

        }
        int width = maxX - minX;
        int height = maxY - minY;
        if (width > 0 && height > 0)
            bitmap2 = Bitmap.createBitmap(bitmap2, minX, minY, width, height);

        return bitmap2;
    }

}