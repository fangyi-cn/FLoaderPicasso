package com.fycmd.imageloader.fpicasso;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

/**
 * Picasso高斯模糊
 * Created by fy on 2018/6/11.
 */
public class PicassoBlurTransformation implements Transformation {
    private RenderScript rs;
    private float blurRadius;

    public PicassoBlurTransformation(Context context, float blurRadius) {
        super();
        rs = RenderScript.create(context);
        this.blurRadius = blurRadius;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Bitmap transform(Bitmap bitmap) {
        Bitmap blurredBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Allocation input = Allocation.createFromBitmap(rs, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);

        //设置模糊半径
        script.setRadius(blurRadius);

        script.forEach(output);

        output.copyTo(blurredBitmap);

        bitmap.recycle();

        return blurredBitmap;
    }

    @Override
    public String key() {
        return "blur";
    }
}
