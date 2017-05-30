/**   
* @Title: Point.java 
* @Package util 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-18
* @version V1.0   
*/
package models;

/**
* <p> ��</p>
* <p>X��������ϵ��£�Y�����������</p>
* @author FlyingFish
* @date 2017-05-18
*/
public class Point {

	public int x;
	public int y;
	
	public Point(int x,int y){
		this.x = x;
		this.y = y;
	}
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean equals(Object o){
		Point p = (Point)o;
		return x == p.x && y == p.y;
	}
}
