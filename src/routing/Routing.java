/**   
* @Title: Routing.java 
* @Package routing 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package routing;

import java.util.ArrayList;

import models.Point;

/**
* <p>·�ɽӿ�</p>
* <p>Ϊ��λ�������ڼ䣬��λ֮���ṩ·��֧��</p>
* @author FlyingFish
* @date 2017-05-20
*/
public interface Routing {
	/** 
	* <p>�����·�� </p> 
	* <p>������㡢�յ� </p> 
	* @param start
	* @param end
	* @return  
	* @see #hops(Point, Point)
	*/
	ArrayList<Point> routing(Point start,Point end);
	
	/** 
	* <p>��ǰ·��������侭�������� </p> 
	* <p>ע�������յ�Ӧ��<Code> Map </Code>�����г��ڡ���ڻ�λԪ�����Ӧ </p> 
	* @param start
	* @param end
	* @return 
	*/
	int hops(Point start,Point end);
}
