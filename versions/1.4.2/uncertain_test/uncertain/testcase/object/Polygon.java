/**
 * Created on: 2004-6-11 14:54:31
 * Author:     zhoufan
 */
package uncertain.testcase.object;

import java.util.*;

import uncertain.composite.*;

/**
 * 
 */
public class Polygon {
	
	// field mapping
	String			name;
	boolean		CanMove;
	static int	OtherAttrib;

	// Element mapping
	Point[]			Points;
	List			PointList;
	
	List	  		points1;
	Point[]			points2;
	CompositeMap	points3;
	Point			center;

	/**
	 * Constructor for Polygon.
	 */
	public Polygon() {

	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public void addCenter(Point p){
		center = p;
	}
	
	public Point getCenter(){
		return center;
	}
	
	public void setPoints1(List cl){
		points1 = cl;
	}
	
	public List getPoints1(){
		return points1;
	}
	
	public void setPoints2(Point[] pa){
		points2 = pa;
	}
	
	public Point[] getPoints2(){
		return points2;
	}
	
	public void setPoints3(CompositeMap p){
		points3 = p;
	}
	
	public CompositeMap getPoints3(){
		return points3;
	}
	
	// these method should not be mapped
	
	public void getSomething(){
	}
	
	public int setSomething(){
		return 0;
	}
	
	public int getFoo( int foo){
		return foo;
	}
	
	public int get(){
		return 0;
	}
	
	public void setFoo( int a, int b){
	}
	
	public void set(int id){
	}
	

}
