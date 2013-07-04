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
	
	public boolean inRange(Position dest, double window, double power) {
		double t = _angle;
		double w2 = window / 2;
		double ra =  enforceAngle(getAngle(dest));
		System.out.printf("a: %f, w: %f, ra: %f     <%f, %f>\n", t, w2, ra, enforceAngle(t + w2), enforceAngle(t - w2));
		System.out.printf("d: %f, p:%f\n", getNorm(dest), power);
		if ((ra <= enforceAngle(t + w2)) && (ra >= enforceAngle(t - w2)))
			System.err.println("Angle OK");
		if ((ra <= enforceAngle(t + w2)) && (ra >= enforceAngle(t - w2)))
			return getNorm(dest) <= power;
		return false;
	}
	
	public static final OrientedPosition ORIGIN = new OrientedPosition(0, 0, 0);

	public static OrientedPosition from(Position pos, double angle) {
		return new OrientedPosition(pos._x, pos._y, angle);
	}
	
	public String toString() {
		return String.format("<x=%d, y=%d, a=%f>", _x, _y, _angle);
	}
}
