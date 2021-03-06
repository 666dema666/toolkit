/*
 * Copyright (c) 1998-2014 by Richard A. Wilkes. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * version 2.0. If a copy of the MPL was not distributed with this file, You
 * can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as defined
 * by the Mozilla Public License, version 2.0.
 */

package com.trollworks.toolkit.utility.units;

import com.trollworks.toolkit.annotation.Localize;
import com.trollworks.toolkit.utility.Localization;
import com.trollworks.toolkit.utility.text.Numbers;

import java.text.MessageFormat;

/** Common length units. */
public enum LengthUnits implements Units {
	/** Points (1/72 of an inch). */
	PT(1.0 / 72.0) {
		@Override
		public String getLocalizedName() {
			return POINTS_DESCRIPTION;
		}
	},
	/** Inches. */
	IN(1.0) {
		@Override
		public String getLocalizedName() {
			return INCHES_DESCRIPTION;
		}
	},
	/** Feet. */
	FT(12.0) {
		@Override
		public String getLocalizedName() {
			return FEET_DESCRIPTION;
		}
	},
	/** Feet and Inches */
	FT_IN(1.0) {
		@Override
		public String getLocalizedName() {
			return FEET_AND_INCHES_NAME;
		}

		@Override
		public String getDescription() {
			return FEET_AND_INCHES_DESCRIPTION;
		}

		@Override
		public String format(double value, boolean localize) {
			int feet = (int) (Math.floor(value) / 12);
			value -= 12.0 * feet;
			if (feet > 0) {
				String buffer = formatNumber(feet, localize) + '\'';
				if (value > 0) {
					return buffer + ' ' + formatNumber(value, localize) + '"';
				}
				return buffer;
			}
			return formatNumber(value, localize) + '"';
		}

		private String formatNumber(double value, boolean localize) {
			return Numbers.trimTrailingZerosAfterDecimal(localize ? Numbers.format(value) : Double.toString(value), localize);
		}
	},
	/** Yards. */
	YD(36.0) {
		@Override
		public String getLocalizedName() {
			return YARDS_DESCRIPTION;
		}
	},
	/** Miles. */
	MI(5280.0 * 12.0) {
		@Override
		public String getLocalizedName() {
			return MILES_DESCRIPTION;
		}
	},
	/** Millimeters. */
	MM(0.1 / 2.54) {
		@Override
		public String getLocalizedName() {
			return MILLIMETERS_DESCRIPTION;
		}
	},
	/** Centimeters. */
	CM(1.0 / 2.54) {
		@Override
		public String getLocalizedName() {
			return CENTIMETERS_DESCRIPTION;
		}
	},
	/** Kilometers. */
	KM(100000.0 / 2.54) {
		@Override
		public String getLocalizedName() {
			return KILOMETERS_DESCRIPTION;
		}
	},
	/** Meters. Must be after all the other 'meter' types. */
	M(100.0 / 2.54) {
		@Override
		public String getLocalizedName() {
			return METERS_DESCRIPTION;
		}
	};

	@Localize("Points")
	@Localize(locale = "de", value = "Punkte")
	static String	POINTS_DESCRIPTION;
	@Localize("Inches")
	@Localize(locale = "de", value = "Zoll")
	static String	INCHES_DESCRIPTION;
	@Localize("Feet")
	@Localize(locale = "de", value = "Fuß")
	static String	FEET_DESCRIPTION;
	@Localize("Feet & Inches")
	@Localize(locale = "de", value = "Fuß & Zoll")
	static String	FEET_AND_INCHES_NAME;
	@Localize("Feet (') & Inches (\")")
	@Localize(locale = "de", value = "Fuß (') & Zoll (\")")
	static String	FEET_AND_INCHES_DESCRIPTION;
	@Localize("Yards")
	@Localize(locale = "de", value = "Schritt")
	static String	YARDS_DESCRIPTION;
	@Localize("Miles")
	@Localize(locale = "de", value = "Meilen")
	static String	MILES_DESCRIPTION;
	@Localize("Millimeters")
	@Localize(locale = "de", value = "Millimeter")
	static String	MILLIMETERS_DESCRIPTION;
	@Localize("Centimeters")
	@Localize(locale = "de", value = "Zentimeter")
	static String	CENTIMETERS_DESCRIPTION;
	@Localize("Meters")
	@Localize(locale = "de", value = "Meter")
	static String	METERS_DESCRIPTION;
	@Localize("Kilometers")
	@Localize(locale = "de", value = "Kilometer")
	static String	KILOMETERS_DESCRIPTION;
	@Localize("{0} {1}")
	static String	FORMAT;
	@Localize("%s (%s)")
	static String	DESCRIPTION_FORMAT;

	static {
		Localization.initialize();
	}

	private double	mFactor;

	private LengthUnits(double factor) {
		mFactor = factor;
	}

	@Override
	public double convert(Units units, double value) {
		return value * units.getFactor() / mFactor;
	}

	@Override
	public double normalize(double value) {
		return value / mFactor;
	}

	@Override
	public double getFactor() {
		return mFactor;
	}

	@Override
	public String format(double value, boolean localize) {
		String textValue = localize ? Numbers.format(value) : Double.toString(value);
		return MessageFormat.format(FORMAT, Numbers.trimTrailingZerosAfterDecimal(textValue, localize), getAbbreviation());
	}

	@Override
	public LengthUnits[] getCompatibleUnits() {
		return values();
	}

	@Override
	public String getAbbreviation() {
		return name().toLowerCase();
	}

	@Override
	public String getDescription() {
		return String.format(DESCRIPTION_FORMAT, getLocalizedName(), getAbbreviation());
	}
}
