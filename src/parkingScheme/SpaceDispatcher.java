/**   
* @Title: SpaceDispatcher.java 
* @Package parkingScheme 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package parkingScheme;

import models.DispatchState;
import models.Task;
import models.VRP;

/**
* <p> 车位调度组件</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-20
*/
public interface SpaceDispatcher {
	
	
	/** 
	* <p>通知停车场方面入库/出库事件 </p> 
	* <p>Description: </p> 
	* @param task 
	*/
	void event(Task task);
	

	/** 
	* <p>使停车位回到当初某个状态</p> 
	* <p>Description: </p> 
	* @param restoreTime 当时的系统时间
	* @param deprivedEventsNumber 回退的事件数目
	*/
	void restore(int restoreTime,int deprivedEventsNumber);
	
	/** 
	* <p>车位调度 </p>  
	* @param vrps 全部申请
	* @param id 当前申请编号
	* @param readyTime 申请时间，为执行入库的机器人在起点就绪的时间,每次调用时的就绪时间应该是递增的
	* @return 
	*/
	DispatchState parkingSpaceDispatch(VRP[] vrps,int id,int readyTime);
}
