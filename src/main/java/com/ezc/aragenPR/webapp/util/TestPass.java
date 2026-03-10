package com.ezc.aragenPR.webapp.util;

import java.util.regex.Pattern;

public class TestPass {

	public static void main(String[] args) {
		//System.out.print(new EzCipher().ezEncrypt("104"));
		Pattern regex = Pattern.compile("^[a-zA-Z0-9]*$");
		String your_string = "abcjkj ";
		if (!regex.matcher(your_string).find()) {
		    System.out.print("::::special charcters");
		}

	}

}
