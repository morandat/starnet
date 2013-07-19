package fr.labri.starnet;

import java.awt.Dimension;

public class Position {
	final int _x;
	final int _y;
	
	public Position(int x, int y) {
		_x = x;
		_y = y;
	}
	
	public final int getX() {
		return _x;
	}
	
	public final int getY() {
		return _y;
	}
	
	public final double getAngle() {
		return ORIGIN.getAngle(this);
	}
	
	public final double getNorm() {
		return ORIGIN.getNorm(this);
	}
	
	public final double getAngle(Position origin) {
		return getAngle(origin._x, origin._y);
	}
	
	public final double getAngle(int x, int y) {
		int xx = x - _x;
		int yy = y - _y;
		return Math.atan2(yy, xx);
	}
	
	public final double getNorm(Position origin) {
		return getNorm(origin._x, origin._y);
	}
	
	public final double getNorm(int x, int y) {
		int xx = _x - x;
		int yy = _y - y;
		return Math.sqrt(xx * xx + yy * yy);
	}
	
	public final long cross(Position other) {
		return cross(other._x, other._y);
	}
	
	public final long cross(int x, int y) {
		return _x * y - _y * x;
	}
			
	public final long dotProduct() {
		return dotProduct(ORIGIN); 
	}
	
	public final long dotProduct(Position other) {
		return dotProduct(other._x, other._y);
	}
	
	public final long dotProduct(int x, int y) {
		return _x*x + _y * y; 
	}
	
	final public Position vector(Position other) {
		return vector(other._x, other._y);
	}	
	
	final public Position vector(int x, int y) {
		return new Position(x - _x, y - _y);
	}
	
	public static final Position ORIGIN = new Position(0, 0);
	
	public Dimension toDimension() {
		return new Dimension(_x, _y);
	}
	
	public String toString() {
		return String.format("<x=%d, y=%d>", _x, _y);
	}

	public long dimension() {
		return _x * _y;
	}
}
