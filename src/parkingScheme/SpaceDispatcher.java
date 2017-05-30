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
	
	boolean restore(int restoreTime);
	
	/** 
	* <p>��λ���� </p>  
	* @param vrps ȫ������
	* @param id ��ǰ������
	* @param readyTime ����ʱ�䣬Ϊִ�����Ļ���������������ʱ��,ÿ�ε���ʱ�ľ���ʱ��Ӧ���ǵ�����
	* @return 
	*/
	DispatchState parkingSpaceDispatch(VRP[] vrps,int id,int readyTime);
}