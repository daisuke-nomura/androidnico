package nomula.Nico;

import java.util.HashMap;

import android.graphics.Bitmap;

class ImageCache{
	private static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	public static Bitmap getImage(String key){
		if (cache.containsKey(key))
			return cache.get(key);

		return null;
	}

	public static void setImage(String key, Bitmap image) {
		cache.put(key, image);
	}

	public static void clearCache(){
		cache = null;
		cache = new HashMap<String, Bitmap>();
	}
}