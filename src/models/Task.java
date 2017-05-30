/**   
* @Title: Task.java 
* @Package models 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
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
	//���
	public static final int PULL_IN = 0;
	
	//����
	public static final int PULL_OUT = 1;
	
	//�ӳ��ڷ�����ڣ������˸�������
	public static final int E_RETURN = 2;
	
	//�ӳ�λ������ڣ������˸�������
	public static final int PS_RETURN = 3;
	
	public int carID;
	
	/*����������ʼ��ʱ��
	/*���ڳ������񣬿�ʼʱ������в�ͬ��⣬
	 * ����һ���Ǵ���ڳ�����ʱ�䣨Ȼ��ȥ�ӳ����ͳ��⣩��
	 * ������Comparator����á� */
	public int startTime;
	
	/** 
	* @Fields taskType : TODO(�������ͣ���<Code>Task</Code>�ж���) 
	*/ 
	public int taskType;
	
	
	//��λ��ţ���Map����
	public int parkingSpaceID = -1;
	
	/** 
	* <p>���캯�� </p> 
	* <p>���ڸ�������{@link #E_RETURN}����λ�����Ϊ-1</p> 
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
			
	//ִ������Ļ����˱��
	 public int exeRobotID;
	 
	 //ʵ������ʼʱ��
	 public int realStartTime;
	 
	 //ʵ���������ʱ��
	 public int realFinishTime;
	 
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
	}

	/*����ʵ�������ӿ�*/
	
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


	/** (non-Javadoc)
	 * @see dataStructure.other.HeapElement#updateKey(java.lang.Object)
	 */
	@Override
	public void updateKey(Integer key) {
		// TODO Auto-generated method stub
		this.startTime = key;
	}

	/**��������ʼʱ�����Ƚ�
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Task o) {
		// TODO Auto-generated method stub
		return this.startTime - o.startTime;
	}
	
}
