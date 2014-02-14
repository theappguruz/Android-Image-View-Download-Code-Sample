package com.theappguruz.imagedownload;

public class Constant {

	public static String IMAGE_BASE_URL = "http://www.theappguruz.com/android/demo_app/images";

	// here we are created folder with package name so when user uninstall the
	// application remove all the storage data of that application
	public static String STORE_IN_FOLDER = android.os.Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.theappguruz/theappguruz";
}
