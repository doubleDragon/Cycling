package com.android.cycling.util;

import java.io.IOException;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageUtils {

	/**
	 * Load a picture from the resource.
	 * 
	 * @param resources
	 * @param resourceId
	 * @return
	 */
	public static Bitmap loadBitmapFromResource(Resources resources,
			int resourceId) {
		// Not scale the bitmap. This will turn the bitmap in raw pixel unit.
		Options options = new BitmapFactory.Options();
		options.inScaled = false;
		// Load the bitmap object.
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId,
				options);
		// Set the density to NONE. This is needed for the ImageView to not
		// scale.
		bitmap.setDensity(Bitmap.DENSITY_NONE);

		return bitmap;
	}

	/**
	 * Load a picture from assets.
	 * <p>
	 * This method will throw RuntimeException if the image file does not exist.
	 * 
	 * @param context
	 * @param assetPath
	 *            The image file path in the assets folder. For example, if the
	 *            image file is <code>assets/images/abc.png</code>, then
	 *            assetPath is <code>images/abc.png</code>
	 * @return
	 */
	public static Bitmap loadBitmapFromAssetNoThrow(Context context,
			String assetPath) {
		try {
			return loadBitmapFromAsset(context, assetPath);
		} catch (IOException e) {
			throw ErrorUtils.runtimeException(e);
		}
	}

	/**
	 * Load a picture from assets.
	 * 
	 * @param context
	 * @param assetPath
	 *            The image file path in the assets folder. For example, if the
	 *            image file is <code>assets/images/abc.png</code>, then
	 *            assetPath is <code>images/abc.png</code>
	 * @return
	 * @throws IOException
	 */
	public static Bitmap loadBitmapFromAsset(Context context, String assetPath)
			throws IOException {
		return BitmapFactory.decodeStream(context.getAssets().open(assetPath));
	}

	/**
	 * Load Bitmap from the internet.
	 * 
	 * @param imageUrl
	 * @return
	 * @throws IOException
	 */
	public static Bitmap loadBitmapFromInternet(String imageUrl)
			throws IOException {
		URL url = new URL(imageUrl);
		return BitmapFactory
				.decodeStream(url.openConnection().getInputStream());
	}

	/**
	 * Load Bitmap from the internet.
	 * 
	 * @param imageUrl
	 * @param defaultValue
	 *            Value to be returned if loading fails.
	 * @return Bitmap object or null.
	 */
	public static Bitmap loadBitmapFromInternet(String imageUrl,
			Bitmap defaultValue) {
		Bitmap bitmap = defaultValue;
		try {
			bitmap = loadBitmapFromInternet(imageUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * Calculate ratio to scale an object to fit the max size (width or height). <br/>
	 * Usage example: <br/>
	 * 
	 * <pre>
	 * float ratio = sizeFitRatio(width, height, maxWidth, maxHeight);
	 * // Calculate size to fit maximum width or height.
	 * int newWidth = ratio * width;
	 * int newHeight = ratio * height;
	 * </pre>
	 * 
	 * @param objectWidth
	 * @param objectHeight
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static float sizeFitRatio(int objectWidth, int objectHeight,
			int maxWidth, int maxHeight) {
		float ratio = 1f; // Default ratio to 1.
		if (objectWidth != 0 && objectHeight != 0) {
			float ratioWidth = maxWidth / (float) objectWidth;
			float ratioHeight = maxHeight / (float) objectHeight;
			float minRatio = (ratioWidth < ratioHeight) ? ratioWidth
					: ratioHeight;
			if (minRatio > 0) {
				ratio = minRatio;
			}
		}
		return ratio;
	}

	/**
	 * Scale an bitmap to fit frame size.
	 * 
	 * @param bitmap
	 * @param frameWidth
	 * @param frameHeight
	 * @return Scaled bitmap, or the bitmap itself if scaling is unnecessary.
	 */
	public static Bitmap scaleToFitFrame(Bitmap bitmap, int frameWidth,
			int frameHeight) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		float ratio = sizeFitRatio(bitmapWidth, bitmapHeight, frameWidth,
				frameHeight);
		return scaleImage(bitmap, ratio);
	}

	/**
	 * Scale a bitmap.
	 * 
	 * @param bitmap
	 * @param ratio
	 * @return
	 */
	public static Bitmap scaleImage(Bitmap bitmap, float ratio) {
		Bitmap result = bitmap;
		if (ratio != 1f) {
			int newWidth = (int) (bitmap.getWidth() * ratio);
			int newHeight = (int) (bitmap.getHeight() * ratio);
			result = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight,
					true);
		}
		return result;
	}

	/**
	 * Create rounded corner bitmap from original bitmap.
	 * <p>
	 * Reference
	 * http://stackoverflow.com/questions/2459916/how-to-make-an-imageview
	 * -to-have-rounded-corners
	 * 
	 * @param input
	 *            Original bitmap.
	 * @param cornerRadius
	 *            Corner radius in pixel.
	 * @param w
	 * @param h
	 * @param squareTL
	 * @param squareTR
	 * @param squareBL
	 * @param squareBR
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap input,
			float cornerRadius, int w, int h, boolean squareTL,
			boolean squareTR, boolean squareBL, boolean squareBR) {

		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);

		// make sure that our rounded corner is scaled appropriately
		final float roundPx = cornerRadius;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		// draw rectangles over the corners we want to be square
		if (squareTL) {
			canvas.drawRect(0, 0, w / 2, h / 2, paint);
		}
		if (squareTR) {
			canvas.drawRect(w / 2, 0, w, h / 2, paint);
		}
		if (squareBL) {
			canvas.drawRect(0, h / 2, w / 2, h, paint);
		}
		if (squareBR) {
			canvas.drawRect(w / 2, h / 2, w, h, paint);
		}

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(input, 0, 0, paint);

		return output;
	}

}