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

import com.trollworks.toolkit.utility.text.Enums;
import com.trollworks.toolkit.utility.text.Numbers;

/** Holds a value and {@link LengthUnits} pair. */
public class LengthValue extends UnitsValue<LengthUnits> {
	/**
	 * @param buffer The buffer to extract a {@link LengthValue} from.
	 * @param localized <code>true</code> if the string might have localized notation within it.
	 * @return The result.
	 */
	public static LengthValue extract(String buffer, boolean localized) {
		LengthUnits units = LengthUnits.IN;
		if (buffer != null) {
			buffer = buffer.trim();
			// Check for the special case of FEET_AND_INCHES first
			int feetMark = buffer.indexOf('\'');
			int inchesMark = buffer.indexOf('"');
			if (feetMark != -1 || inchesMark != -1) {
				if (feetMark == -1) {
					String part = buffer.substring(0, inchesMark);
					return new LengthValue(localized ? Numbers.getLocalizedDouble(part, 0) : Numbers.getDouble(part, 0), LengthUnits.FT_IN);
				}
				String part = buffer.substring(inchesMark != -1 && feetMark > inchesMark ? inchesMark + 1 : 0, feetMark);
				double inches = (localized ? Numbers.getLocalizedDouble(part, 0) : Numbers.getDouble(part, 0)) * 12;
				if (inchesMark != -1) {
					part = buffer.substring(feetMark < inchesMark ? feetMark + 1 : 0, inchesMark);
					inches += localized ? Numbers.getLocalizedDouble(part, 0) : Numbers.getDouble(part, 0);
				}
				return new LengthValue(inches, LengthUnits.FT_IN);
			}
			for (LengthUnits lu : LengthUnits.values()) {
				String text = Enums.toId(lu);
				if (buffer.endsWith(text)) {
					units = lu;
					buffer = buffer.substring(0, buffer.length() - text.length());
					break;
				}
			}
		}
		return new LengthValue(localized ? Numbers.getLocalizedDouble(buffer, 0) : Numbers.getDouble(buffer, 0), units);
	}

	/**
	 * Creates a new {@link UnitsValue}.
	 *
	 * @param value The value to use.
	 * @param units The {@link Units} to use.
	 */
	public LengthValue(double value, LengthUnits units) {
		super(value, units);
	}

	/**
	 * Creates a new {@link LengthValue} from an existing one.
	 *
	 * @param other The {@link LengthValue} to clone.
	 */
	public LengthValue(LengthValue other) {
		super(other);
	}

	@Override
	public LengthUnits getDefaultUnits() {
		return LengthUnits.FT_IN;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof LengthValue && super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
