/**   
* @Title: DispatchState.java 
* @Package models 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-24
* @version V1.0   
*/
package models;

/**
* <p> DispatchState</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-24
*/
public class DispatchState {

	public boolean success ;
	public int parkingSpaceID;
	public int delay;
	
	public DispatchState(boolean s,int p,int d){
		success = s;
		parkingSpaceID = p;
		delay = d;
	}
}
