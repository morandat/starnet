package fr.labri.starnet;

public class OrientedPosition extends Position {
	final double _angle;
	
	public OrientedPosition(int x, int y, double angle) {
		super(x, y);
		
		_angle = enforceAngle(angle);
	}
	
	public double getOrientation() {
		return _angle;
	}

	public int getOrientationAsDegree() {
		return (int)(int)Math.round(Math.toDegrees(_angle));
	}
	
	public final static double enforceAngle(double angle) {
		return (2*Math.PI + angle) % (2*Math.PI);
	}
	
	public OrientedPosition rotate(double angle) {
		return from(this, angle + _angle);
	}
	
	public boolean contains(Position dest, double window, double power) {
		double r = getNorm(dest);
		if(r > power)
			return false;
//		System.out.println(this);
//		System.out.println(dest);
		double t = _angle;
		double w2 = window / 2;
		
		double a1 = enforceAngle(t - w2);
//		double a2 = enforceAngle(t - w2);
		
		Position v1 = new Position((int) (Math.cos(a1) * r), -(int)(Math.sin(a1) * r));
//		Position v2 = new Position((int) (Math.cos(a2) * r), -(int)(Math.sin(a2) * r));

		Position v = vector(dest);
//		System.out.println(v1);
//		System.out.println(v);
//		System.out.println(v2);
//		System.out.println("----------------");
//		System.out.println("t:"+Math.toDegrees(window));
		double a = Math.acos(v1.dotProduct(v) / (r*r));
//		double b = Math.asin(v.cross(v1) / (r*r));
//		System.out.println(Math.toDegrees(a));
//		System.out.println(Math.toDegrees(b));
//		System.out.println(v.getAngle(v1));
		//System.out.println(v1.getAngle(v2));
//		System.out.println((a > 0 && a < window)? "ok": "diff");
//		System.out.println("----------------");
		return 	(a > 0 && a < window);
//		return Math.signum(v1.dotProduct(v)) > 0 && Math.signum(v.dotProduct(v2)) > 0;
//		
//		double x1 = Math.cos(a1) * r + _x;
//		double x2 = Math.cos(a2) * r + _x;
//		System.out.printf("cos: %f %f\n", Math.cos(a1), Math.cos(a2));
//		double x = dest._x;
//		System.out.printf("x: %f <%f, %f>\n", x, x1, x2);
//		if(!(x <= x1 && x >= x2))
//			return false;
//	
//		System.out.printf("sin: %f %f\n", Math.sin(a1), Math.sin(a2));
//		double y1 = Math.sin(a1) * r + _y;
//		double y2 = Math.sin(a2) * r + _y;
//		double y = dest._y;
//		System.out.printf("y: %f <%f, %f>\n", y, y1, y2);
//		return (y <= y1 && y >= y2);
	}
	
	public static final OrientedPosition ORIGIN = new OrientedPosition(0, 0, 0);

	public static OrientedPosition from(Position pos, double angle) {
		return new OrientedPosition(pos._x, pos._y, angle);
	}
	
	public String toString() {
		return String.format("<x=%d, y=%d, a=%f>", _x, _y, _angle);
	}
}
