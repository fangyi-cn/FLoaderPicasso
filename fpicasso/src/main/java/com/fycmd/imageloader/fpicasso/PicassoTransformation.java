package com.fycmd.imageloader.fpicasso;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.fycmd.imageloader.floaderlib.base.FTransformation;
import com.squareup.picasso.Transformation;

/**
 * picasso加载圆角图片
 */
public class PicassoTransformation implements Transformation, FTransformation {
    private float bitmapAngle;

    protected PicassoTransformation(float corner) {
        this.bitmapAngle = corner;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        float roundPx = bitmapAngle;
        //圆角的横向半径和纵向半径
        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);
        source.recycle();
        return output;
    }

    @Override
    public String key() {
        return "bitmapAngle()";
    }
}
