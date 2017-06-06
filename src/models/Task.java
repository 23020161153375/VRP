/**   
* @Title: Task.java 
* @Package models 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package models;

import dataStructure.other.HeapElement;
import routing.Routing;

/**
* <p> Task</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-20
*/
public class Task implements Comparable<Task>, HeapElement<Integer> {
	//入库
	public static final int PULL_IN = 0;
	
	//出库
	public static final int PULL_OUT = 1;
	
	//从出口返回入口，机器人附加任务
	//public static final int E_RETURN = 2;
	
	//从车位返回入口，机器人附加任务
	//public static final int PS_RETURN = 3;
	
	public int carID;
	
	/*任务正常开始的时间
	 * 这里用Comparator会更好。 */
	public int startTime;
	
	/** 
	* @Fields taskType : TODO(任务类型，在<Code>Task</Code>中定义) 
	*/ 
	public int taskType;
	
	
	//车位编号，由Map决定
	public int parkingSpaceID = -1;
	
	/** 
	* <p>构造函数 </p> 
	* <p>对于附加任务{@link #E_RETURN}，车位编号置为-1</p> 
	* @param carID
	* @param startTime
	* @param taskType
	* @param parkingSpaceID 
	*/
	public Task(int carID,int startTime,int taskType,int parkingSpaceID){
		this.carID = carID;
		this.startTime = startTime;
		this.taskType = taskType;
		this.parkingSpaceID = parkingSpaceID;
	}
			
	//执行任务的机器人编号
	 public int exeRobotID;
	 
	 //实际任务开始时间
	 public int realStartTime;
	 
	 //实际任务完成时间
	 public int realFinishTime;
	 
/*	 public String toString(){
		 if(taskType == Task.PULL_IN)
		 return null;
	 }*/
	 
	 /*
	public static int realFinishTime(Task task,Routing router,Map map){
		Point start ,end;
		if(task.taskType == Task.PULL_IN) { 
			start = map.in;
			end = map.allSpaces.get(task.parkingSpaceID).location;
		}else if(task.taskType == Task.PULL_OUT) {
			start = map.allSpaces.get(task.parkingSpaceID).location;
			end = map.out;
		}else if(task.taskType == Task.E_RETURN){
			start = map.out;
			end = map.in;
		}else {
			start = map.allSpaces.get(task.parkingSpaceID).location;;
			end = map.in;
		}
		
		return task.realStartTime + router.hops(start, end);
	}*/

	/*下面实现两个接口*/
	
	private int loc ;
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

	private int startTimeFromEntrance;
	
	public int startTimeFromEntrance(){
		return this.startTimeFromEntrance;
	}
	
	/** (non-Javadoc)
	 * @see dataStructure.other.HeapElement#updateKey(java.lang.Object)
	 */
	@Override
	public void updateKey(Integer key) {
		// TODO Auto-generated method stub
		this.startTimeFromEntrance = key;
	}

	/**以正常开始时间来比较
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Task o) {
		// TODO Auto-generated method stub
		return this.startTimeFromEntrance - o.startTimeFromEntrance;
	}
	
}
