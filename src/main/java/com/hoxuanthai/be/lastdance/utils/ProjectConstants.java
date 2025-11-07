package com.hoxuanthai.be.lastdance.utils;

import java.util.Locale;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
public final class ProjectConstants {

	public static final String DEFAULT_ENCODING = "UTF-8";

	public static final Locale TURKISH_LOCALE = new Locale.Builder().setLanguage("tr").setRegion("TR").build();

	private ProjectConstants() {

		throw new UnsupportedOperationException();
	}

}
