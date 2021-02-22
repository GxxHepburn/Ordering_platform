package com.gxx.ordering_platform;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class Test {

	
	public static void main(String[] args) throws Exception {
		List<Integer> integers = new ArrayList<Integer>();
		integers.add(1);
		integers.add(2);
		JSONArray inJsonArray = new JSONArray(integers);
		for (int i = 0; i <inJsonArray.length(); i++) {
			System.out.println(inJsonArray.get(i));
		}
		System.out.println(inJsonArray);
	}
}
