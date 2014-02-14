package com.theappguruz.imagedownload;

import java.util.ArrayList;
import java.util.Random;

import com.theappguruz.R;

public class Images {

	private Random randNo;
	private ArrayList<String> imageName;

	public Images() {
		imageName = new ArrayList<String>();
		imageName.add("a1");
		imageName.add("a2");
		imageName.add("a3");
		imageName.add("a4");
		imageName.add("a5");
	}

	public String getImageId() {
		randNo = new Random();
		return imageName.get(randNo.nextInt(imageName.size()));
	}
}
