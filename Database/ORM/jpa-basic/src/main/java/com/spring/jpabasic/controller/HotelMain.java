package com.spring.jpabasic.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.spring.jpabasic.service.GetHotelSummaryService;
import com.spring.jpabasic.utils.EMFUtils;

public class HotelMain {
	private GetHotelSummaryService hotelSummaryService = new GetHotelSummaryService();

	public static void main (String[] args) throws IOException {
		EMFUtils.init();

	}
}
