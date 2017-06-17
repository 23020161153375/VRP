/**   
* @Title: SpaceDispatcher.java 
* @Package parkingScheme 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package parkingScheme;

import models.DispatchState;
import models.Task;
import models.VRP;

/**
* <p> ��λ�������</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-20
*/
public interface SpaceDispatcher {
	
	
	/** 
	* <p>֪ͨͣ�����������/�����¼� </p> 
	* <p>Description: </p> 
	* @param task 
	*/
	void event(Task task);
	

	/** 
	* <p>ʹͣ��λ�ص�����ĳ��״̬</p> 
	* <p>Description: </p> 
	* @param restoreTime ��ʱ��ϵͳʱ��
	* @param deprivedEventsNumber ���˵��¼���Ŀ
	*/
	void restore(int restoreTime,int deprivedEventsNumber);
	
	/** 
	* <p>��λ���� </p>  
	* @param vrps ȫ������
	* @param id ��ǰ������
	* @param readyTime ����ʱ�䣬Ϊִ�����Ļ���������������ʱ��,ÿ�ε���ʱ�ľ���ʱ��Ӧ���ǵ�����
	* @return 
	*/
	DispatchState parkingSpaceDispatch(VRP[] vrps,int id,int readyTime);
}
