package searcher1;

import java.util.Set;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.uno.UnoRuntime;

public class TextSelector {
	
	private static void select(int oldPosition, int newPosition, boolean select, XTextCursor cursor) {
		int delta = newPosition - oldPosition;
		if (delta >= 0)
			cursor.goRight((short) delta, select);
		else
			cursor.goLeft((short) (-delta), select);
	}
	
	public static void selectTextRanges(XText text, Set<Pair<Integer, Integer>> ranges, String fontName)
			throws IllegalArgumentException, UnknownPropertyException, PropertyVetoException, WrappedTargetException {
		int position = 0;
		XTextCursor cursor = text.createTextCursor();
	    XPropertySet properties = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, cursor);
	    cursor.gotoStart(false);
		for (Pair<Integer, Integer> pair : ranges) {
			TextSelector.select(position, pair.first, false, cursor);
			position = pair.first;
			TextSelector.select(position, pair.second, true, cursor);
			position = pair.second;
			properties.setPropertyValue("CharFontName", fontName);
		}
	}
}