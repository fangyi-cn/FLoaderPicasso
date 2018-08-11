package com.fycmd.imageloader.fpicasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.fycmd.imageloader.floaderlib.FLoader;
import com.fycmd.imageloader.floaderlib.base.FTransformation;
import com.fycmd.imageloader.floaderlib.base.ILoaderFactory;
import com.fycmd.imageloader.floaderlib.base.LoaderBuilder;
import com.fycmd.imageloader.floaderlib.base.Target;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.io.File;

/**
 * Created by fy on 2018/6/9.
 *
 */
public class PicassoLoader implements ILoaderFactory {
    private volatile static Picasso sPicassoSingleton;
    private final String PICASSO_CACHE = "picasso-cache";
    private static LruCache sLruCache = new LruCache(FLoader.getInstance().getContext());

    private static Picasso getPicasso() {
        if (sPicassoSingleton == null) {
            synchronized (PicassoLoader.class) {
                if (sPicassoSingleton == null) {
                    sPicassoSingleton = new Picasso.Builder(FLoader.getInstance().getContext()).memoryCache(sLruCache).build();
                }
            }
        }
        return sPicassoSingleton;
    }

    @Override
    public void clearMemoryCache() {
        sLruCache.clear();
    }

    @Override
    public void clearDiskCache() {
        File diskFile = new File(FLoader.getInstance().getContext().getCacheDir(), PICASSO_CACHE);
        if (diskFile.exists()) {
            //这边自行写删除代码
            // FileUtil.deleteFile(diskFile);
        }
    }

    @Override
    public void load(LoaderBuilder options) {
        RequestCreator requestCreator = null;
        if (options.url != null) {
            requestCreator = getPicasso().load(options.url);
        } else if (options.file != null) {
            requestCreator = getPicasso().load(options.file);
        } else if (options.drawableResId != 0) {
            requestCreator = getPicasso().load(options.drawableResId);
        } else if (options.uri != null) {
            requestCreator = getPicasso().load(options.uri);
        }
        if (requestCreator == null) {
            throw new NullPointerException("requestCreator must not be null");
        }
        if (options.cutHeight > 0 && options.cutWidth > 0) {
            requestCreator.resize(options.cutWidth, options.cutHeight);
        }
        if (options.isCenterInside) {
            requestCreator.centerInside();
        } else if (options.isCenterCrop) {
            requestCreator.centerCrop();
        }
        if (options.config != null) {
            requestCreator.config(options.config);
        }
        if (options.errorResId != 0) {
            requestCreator.error(options.errorResId);
        }
        if (options.placeholderResId != 0) {
            requestCreator.placeholder(options.placeholderResId);
        }
        if (options.bitmapAngle != 0) {
            requestCreator.transform(new PicassoTransformation(options.bitmapAngle));
        }
        if (options.skipLocalCache) {
            requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
        }
        if (options.skipMemCache) {
            requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
        }
        if (options.degrees != 0) {
            requestCreator.rotate(options.degrees);
        }
        if (options.usingBlur) {
            if (options.context != null) {
                requestCreator.transform(new PicassoBlurTransformation(options.targetView.getContext(), options.blurRadius));
            } else {
                throw new RuntimeException("if you want to use blurTransformation, you must call the method 'with(Context context)'.or do not call method 'usingBlur()'.. ");
            }
        }
        if (options.transformations != null) {
            for (FTransformation transformation : options.transformations) {
                if (transformation instanceof Transformation) {
                    requestCreator.transform((Transformation) transformation);
                } else {
                    throw new RuntimeException("transformation must be implements 'com.squareup.picasso.Transformation'");
                }
            }
        }
        if (options.targetView instanceof ImageView) {
            requestCreator.into(((ImageView) options.targetView));
        } else if (options.target != null) {
            requestCreator.into(new PicassoTarget(options.target));
        }
    }

    /**
     * picasso的Target回调方式加载
     */
    class PicassoTarget implements com.squareup.picasso.Target {
        Target target;

        protected PicassoTarget(Target target) {
            this.target = target;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (this.target != null) {
                this.target.onBitmapLoaded(bitmap);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            if (this.target != null) {
                this.target.onBitmapFailed(errorDrawable);
            }
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

}
