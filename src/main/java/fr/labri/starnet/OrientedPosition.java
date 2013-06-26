package fr.labri.starnet;

public class OrientedPosition extends Position {
	final double _angle;
	
	public OrientedPosition(int x, int y, double angle) {
		super(x, y);
		_angle = angle;
	}
	
	double getOrientation() {
		return _angle;
	}

	int getOrientationAsDegree() {
		return (int)(int)Math.round(Math.toDegrees(_angle));
	}
	
	public OrientedPosition rotate(double angle) {
		return from(this, (angle + _angle) % (2 * Math.PI) - Math.PI);
	}
	
	boolean inRange(Position dest, double angle, double power) {
		double t = _angle;
		double a2 = angle / 2;
		double ra = getAngle(dest);
		if ((ra <= (t + a2)) && (ra >= (t - a2)))
			return getNorm(dest) < power;
		return false;
	}
	
	public static final OrientedPosition ORIGIN = new OrientedPosition(0, 0, 0);

	public static OrientedPosition from(Position pos, double angle) {
		return new OrientedPosition(pos._x, pos._y, angle);
	}
}
