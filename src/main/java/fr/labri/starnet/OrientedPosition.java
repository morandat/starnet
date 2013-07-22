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
		//FIXME remove this return to re-enable angle
		if(true)
			return true;
		double t = _angle;
		double w2 = window / 2;
		
		Position v = vector(dest);

		double a1 = enforceAngle(t - w2);
		Position v1 = new Position((int) (Math.cos(a1) * r), -(int)(Math.sin(a1) * r));
		long a = v1.cross(v);
		if(a > 0) // FIXME ... isn't it supposed to be the opposite ?!
			return false;

		double a2 = enforceAngle(t + w2);
		Position v2 = new Position((int) (Math.cos(a2) * r), -(int)(Math.sin(a2) * r));
		long b = v.cross(v2);
		
		return b < 0;
	}
	
	public static final OrientedPosition ORIGIN = new OrientedPosition(0, 0, 0);

	public static OrientedPosition from(Position pos, double angle) {
		return new OrientedPosition(pos._x, pos._y, angle);
	}
	
	public String toString() {
		return String.format("<x=%d, y=%d, a=%f>", _x, _y, _angle);
	}
}
