package com.theappguruz.imagedownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.theappguruz.R;

public class ImageViewAndDownload extends Activity implements OnClickListener {

	private Button btnImageDownload;
	private ProgressDialog pd;
	private ImageView viewDownloadImage;
	private Images imageId;
	private File folderName;
	private String imageName;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (pd != null) {
					pd.dismiss();
				}
				Utils.showNetworkAlert(ImageViewAndDownload.this);
			} else if (msg.what == 2) {
				if (pd != null) {
					pd.dismiss();
				}
				Utils.displayMessage("Image downloade succesfully",
						ImageViewAndDownload.this);
				// Media scaning
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory())));

			} else if (msg.what == 3) {
				if (pd != null) {
					pd.dismiss();
				}
				Utils.displayMessage("Image already downloaded ",
						ImageViewAndDownload.this);
			} else if (msg.what == 4) {
				if (pd != null) {
					pd.dismiss();
				}
				displayImageFromUrl((Bitmap) msg.obj);
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagedisplay);

		viewDownloadImage = (ImageView) findViewById(R.id.viewImage);
		btnImageDownload = (Button) findViewById(R.id.btnImageDownload);
		imageId = new Images();
		imageName = imageId.getImageId();
		LoadImageFromWeb(Constant.IMAGE_BASE_URL + File.separator + imageName);
		btnImageDownload.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v == btnImageDownload) {
			pd = ProgressDialog.show(ImageViewAndDownload.this, "",
					"Downloading Image....", true, false);
			new Thread(new Runnable() {
				public void run() {
					try {

						String imageUrl = Constant.IMAGE_BASE_URL
								+ File.separator + imageName;
						String isDownloded = downloadImage(imageUrl, imageName);
						if (isDownloded.equalsIgnoreCase("complete")) {
							handler.sendEmptyMessage(2);
						} else if (isDownloded.equalsIgnoreCase("")) {
							handler.sendEmptyMessage(3);
						} else {
							handler.sendEmptyMessage(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
						handler.sendEmptyMessage(1);
					}
				}
			}).start();

		}
	}

	// set display image to Imageview
	public void displayImageFromUrl(Bitmap obj) {
		viewDownloadImage.setImageBitmap(obj);
	}

	// image display from the webview
	private void LoadImageFromWeb(final String url1) {
		pd = ProgressDialog.show(ImageViewAndDownload.this, "",
				"Loading Image....", true, false);
		new Thread(new Runnable() {
			public void run() {
				try {

					URL url = new URL(url1);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();

					InputStream is = connection.getInputStream();

					Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;

					BitmapFactory.decodeStream(is, null, options);

					if (options.outWidth > 3000 || options.outHeight > 2000) {
						options.inSampleSize = 4;
					} else if (options.outWidth > 2000
							|| options.outHeight > 1500) {
						options.inSampleSize = 3;
					} else if (options.outWidth > 1000
							|| options.outHeight > 1000) {
						options.inSampleSize = 2;
					}
					// Do the actual decoding
					options.inJustDecodeBounds = false;

					is.close();
					is = getHTTPConnectionInputStream(url1);
					Bitmap myBitmap = BitmapFactory.decodeStream(is, null,
							options);
					is.close();

					if (myBitmap != null) {
						Message msg = new Message();
						msg.obj = myBitmap;
						msg.what = 4;
						handler.sendMessage(msg);
					} else {
						handler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public InputStream getHTTPConnectionInputStream(String url1) {
		URL url;
		InputStream is = null;
		try {
			url = new URL(url1);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			is = connection.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

	// image download code
	public String downloadImage(String imageDownloadUrl, String imageName) {
		// create directory in SDCARD
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			folderName = new File(Constant.STORE_IN_FOLDER);
		else
			folderName = getFilesDir();

		if (!folderName.exists())
			folderName.mkdirs();

		String response = "";
		// create file name and file.
		File storeImageInSDCard = new File(folderName + File.separator
				+ imageName);
		if (!(storeImageInSDCard.exists() && storeImageInSDCard.length() > 0)) {
			// start download image
			response = downloadFile(imageDownloadUrl, imageName,
					folderName.toString());
		}
		return response;
	}

	// start download image
	public String downloadFile(final String url, final String name,
			String foldername) {

		File file;
		FileOutputStream os = null;
		Bitmap myBitmap;
		try {

			URL url1 = new URL(url.replaceAll(" ", "%20"));
			System.out.println("Image url :::" + url1);
			HttpURLConnection urlConnection = (HttpURLConnection) url1
					.openConnection();
			urlConnection.setDoOutput(false);
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			// here create a file which define folder name and image name with
			// extension.
			file = new File(foldername, name + ".jpg");
			InputStream inputStream = urlConnection.getInputStream();
			byte[] buffer = new byte[1024];
			int bufferLength = 0;
			os = new FileOutputStream(file);
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				os.write(buffer, 0, bufferLength);
			}

			os.flush();
			os.close();

			// if image size is too large we can scale image than download.
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			myBitmap = BitmapFactory
					.decodeFile(file.getAbsolutePath(), options);
			if (options.outWidth > 3000 || options.outHeight > 2000) {
				options.inSampleSize = 4;
			} else if (options.outWidth > 2000 || options.outHeight > 1500) {
				options.inSampleSize = 3;
			} else if (options.outWidth > 1000 || options.outHeight > 1000) {
				options.inSampleSize = 2;
			}
			options.inJustDecodeBounds = false;
			myBitmap = BitmapFactory
					.decodeFile(file.getAbsolutePath(), options);

			os = new FileOutputStream(file);
			myBitmap.compress(CompressFormat.JPEG, 90, os);
			os.flush();
			os.close();
			myBitmap.recycle();

			return "complete";
		} catch (SQLException e) {
			e.printStackTrace();
			return "error";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}
}
