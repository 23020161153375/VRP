/**   
* @Title: Calculator.java 
* @Package utils 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-06-03
* @version V1.0   
*/
package utils;

import models.Map;
import models.VRP;
import routing.Routing;

/**
* <p> Calculator</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-06-03
*/
public class Calculator {

	public static int calcT(VRP[] applications,int fPanishment,int fWating){
		int T1 = 0,T2 = 0;
		for(int i = 0;i < applications.length;i ++){
			if(applications[i].refused){
				T2 += fPanishment;
			}else{
				int offset = applications[i].task1.realStartTime - applications[i].task1.startTime;
				if(offset > 0)
					T1 += fWating * offset;
				offset = applications[i].task2.realStartTime - applications[i].task2.startTime;
				if(offset > 0)
					T1 += fWating * offset;
			}			
		}
		
		return T1 + T2;
	}
	
	public static int calcW(VRP[] applications,int fEnergy,Map map, Routing router){
		int W = 0;
		for(int i = 0;i < applications.length;i ++){
			if(!applications[i].refused){

				W += fEnergy * router.hops(map.in, map.allSpaces.get(applications[i].task1.parkingSpaceID).location) ;
				W += fEnergy * router.hops( map.allSpaces.get(applications[i].task1.parkingSpaceID).location, map.out);
			}
		}
		
		return W;
	}
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
