package helper;

import java.awt.Point;
import java.awt.Rectangle;

public class MathHelper {

		public static boolean doRectanglesIntersect(Rectangle a, Rectangle b) {
			return a.intersects(b);
		}
		
		public static boolean isInsideRectangle(Point p, Rectangle r) {
			return p.getX() >= r.getMinX() && p.getX() <= r.getMaxX() 
				&& p.getY() >= r.getMinY() && p.getY() <= r.getMaxY(); 
		}
}
