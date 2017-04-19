package projet_techno_l3.imageeditor.ImageModifications.convolution;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;
import android.support.v8.renderscript.Type;
import android.util.Log;

import projet_techno_l3.imageeditor.ImageModifications.AbstractImageModificationAsyncTask;

/**
 * Created by Antoine Gagnon
 */

public class GaussianBlurRS extends AbstractImageModificationAsyncTask {

    private final int filterSize;

    private Context activitiyContext;


    public GaussianBlurRS(Bitmap src, BlurValues filterSize, Activity activity) {
        super(activity);
        this.src = src;
        this.filterSize = (filterSize.ordinal() *3) + 3;
        this.activitiyContext = activity.getApplicationContext();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        long startTime = System.currentTimeMillis();

        result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        //Create renderscript
        RenderScript rs = RenderScript.create(activitiyContext);

        //Create allocation from Bitmap
        Allocation allocationIn = Allocation.createFromBitmap(rs, src);
        Allocation allocationOut = Allocation.createFromBitmap(rs, result);

        //Create script
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Set blur radius
        blurScript.setRadius(filterSize);

        //Set input for script
        blurScript.setInput(allocationIn);
        //Call script for output allocation
        blurScript.forEach(allocationOut);

        //Copy script result into bitmap
        allocationOut.copyTo(result);

        //Destroy everything to free memory
        allocationIn.destroy();
        allocationOut.destroy();
        blurScript.destroy();
        rs.destroy();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        Log.i("MeanBlurRS", "MeanBlurRS Duration: " + elapsedTime);

        return result;

    }
}