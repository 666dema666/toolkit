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

/** Common weight units. */
public enum WeightUnits implements Units {
	/** Ounces. */
	OZ(1.0 / 16.0) {
		@Override
		public String getLocalizedName() {
			return OUNCES_DESCRIPTION;
		}
	},
	/** Pounds. */
	LB(1.0) {
		@Override
		public String getLocalizedName() {
			return POUNDS_DESCRIPTION;
		}
	},
	/** Short Tons */
	TN(2000.0) {
		@Override
		public String getLocalizedName() {
			return SHORT_TONS_DESCRIPTION;
		}
	},
	/** Long Tons */
	LT(2240.0) {
		@Override
		public String getLocalizedName() {
			return LONG_TONS_DESCRIPTION;
		}
	},
	/** Metric Tons. Must come after Long Tons and Short Tons since it's abbreviation is a subset. */
	T(2205.0) {
		@Override
		public String getLocalizedName() {
			return METRIC_TONS_DESCRIPTION;
		}
	},
	/** Kilograms. */
	KG(2.205) {
		@Override
		public String getLocalizedName() {
			return KILOGRAMS_DESCRIPTION;
		}
	},
	/** Grams. Must come after Kilograms since it's abbreviation is a subset. */
	G(0.002205) {
		@Override
		public String getLocalizedName() {
			return GRAMS_DESCRIPTION;
		}
	};

	@Localize("Ounces")
	@Localize(locale = "de", value = "Unzen")
	static String	OUNCES_DESCRIPTION;
	@Localize("Pounds")
	@Localize(locale = "de", value = "Pfund")
	static String	POUNDS_DESCRIPTION;
	@Localize("Grams")
	@Localize(locale = "de", value = "Gramm")
	static String	GRAMS_DESCRIPTION;
	@Localize("Kilograms")
	@Localize(locale = "de", value = "Kilogramm")
	static String	KILOGRAMS_DESCRIPTION;
	@Localize("Short Tons")
	@Localize(locale = "de", value = "Amerikanische Tonnen")
	static String	SHORT_TONS_DESCRIPTION;
	@Localize("Long Tons")
	@Localize(locale = "de", value = "Britische Tonnen")
	static String	LONG_TONS_DESCRIPTION;
	@Localize("Metric Tons")
	@Localize(locale = "de", value = "Tonnen")
	static String	METRIC_TONS_DESCRIPTION;
	@Localize("{0} {1}")
	static String	FORMAT;
	@Localize("%s (%s)")
	static String	DESCRIPTION_FORMAT;

	private double	mFactor;

	static {
		Localization.initialize();
	}

	private WeightUnits(double factor) {
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
	public WeightUnits[] getCompatibleUnits() {
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
