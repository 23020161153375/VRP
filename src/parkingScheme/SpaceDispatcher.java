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
	
	boolean restore(int restoreTime);
	
	/** 
	* <p>车位调度 </p>  
	* @param vrps 全部申请
	* @param id 当前申请编号
	* @param readyTime 申请时间，为执行入库的机器人在起点就绪的时间,每次调用时的就绪时间应该是递增的
	* @return 
	*/
	DispatchState parkingSpaceDispatch(VRP[] vrps,int id,int readyTime);
}
