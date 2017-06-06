/**   
* @Title: Robot.java 
* @Package models 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-21
* @version V1.0   
*/
package models;

import dataStructure.other.HeapElement;

/**
* <p> Robot</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-21
*/
public class Robot  implements Comparable<Robot>, HeapElement<Integer>{

	public int robotID;
	
	public int completementTime;
	public Point location;
	 
	 /** 
	* <p>构造函数，创建一个机器人 </p> 
	* <p>Description: </p> 
	* @param id 编号
	* @param in 初始位置
	*/
	public Robot(int id,Point in){
		 robotID = id;
		 completementTime = 0;
		 location = in;
	 }
	 
	 private int loc;
	 
	/** (non-Javadoc)
	 * @see dataStructure.other.HeapElement#setElementLocation(int)
	 */
	@Override
	public void setElementLocation(int location) {
		// TODO Auto-generated method stub
		loc = location;
	}
	
	/** (non-Javadoc)
	 * @see dataStructure.other.HeapElement#getElementLocation()
	 */
	@Override
	public int getElementLocation() {
		// TODO Auto-generated method stub
		return loc;
	}
	
	/** (non-Javadoc)
	 * @see dataStructure.other.HeapElement#updateKey(java.lang.Object)
	 */
	@Override
	public void updateKey(Integer key) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("不支持更新");
	}
	
	/** (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Robot o) {
		// TODO Auto-generated method stub
		return completementTime - o.completementTime;
	}

	
}
