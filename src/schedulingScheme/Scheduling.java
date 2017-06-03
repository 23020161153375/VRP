/**   
* @Title: Scheding.java 
* @Package schedulingScheme 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-24
* @version V1.0   
*/
package schedulingScheme;

import models.VRP;

/**
* <p> 机器人调度</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-24
*/
public interface Scheduling {

	/** 
	* <p>调度</p> 
	* <p>Description: </p> 
	* @param nRobots 机器人个数
	* @return 等待代价与拒载代价之和
	*/
	int scheduling(int nRobots);
	
}
