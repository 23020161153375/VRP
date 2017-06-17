/**   
* @Title: Router.java 
* @Package models 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-22
* @version V1.0   
*/
package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import routing.Routing;
import routing.StaticRouting;

/**
* <p> Router</p>
* <p>方便实现<Code> Routing </Code>接口 </p>
* @author FlyingFish
* @date 2017-05-22
*/
public class Router implements Routing {
	public Map parkingLot;
	
	protected int[][] routingI,routingE;
	
	public Router(Map pl,int[][] routingI,int[][] routingE){
		parkingLot = pl;
		this.routingI = routingI;
		this.routingE = routingE;
	}
	
	
	/** (non-Javadoc)
	 * @see routing.Routing#routing(models.Point, models.Point)
	 */
	@Override
	public ArrayList<Point> routing(Point start, Point end) {
		// TODO Auto-generated method stub
		ArrayList<Point>result;
		if(start.equals(end)){
			result =  new ArrayList<Point>();
			result.add(start);
			return result;
		}
		
		boolean startI = parkingLot.search(start) == Map.I,
				startE = parkingLot.search(start) == Map.E,
				startP = parkingLot.search(start) >= 0,
				endI = parkingLot.search(end) == Map.I,
				endE = parkingLot.search(end) == Map.E,
				endP = parkingLot.search(end) >= 0;
				if(startI && endE){//从起点到终点的路径
					return StaticRouting.getRoute(start.x, start.y, end.x, end.y, routingI);
				}else if(startE && endI){
					return StaticRouting.getRoute(end.x, end.y, start.x, start.y, routingE);
				}else if(startI && endP){
					Point endPInlet = parkingLot.allSpaces.get(parkingLot.search(end)).inlet;
					result = StaticRouting.getRoute( start.x, start.y,endPInlet.x,endPInlet.y, routingI) ;
					result.add(end);
				}else if(endE && startP){
					Point startPInlet = parkingLot.allSpaces.get(parkingLot.search(start)).inlet;
					
					//从终点到车位入口的路由
					result = StaticRouting.getRoute(end.x, end.y, startPInlet.x, startPInlet.y,  routingE);
					
					//现在是从终点到车位的路由
					result.add(start);
					
					//倒置
					int size = result.size();
					int mid = (size -1) / 2;
					
					for(int i = 0;i< size;i ++)
						if(i <= mid){
							Point temp = result.get(i);
							result.set(i, result.get(size - 1 - i));
							result.set(size - 1 - i, temp);
						}						
				}else//暂不支持PP之间及起、终点带X的路由
					throw new UnsupportedOperationException();		
			return result;
	}

	/** 
	 * @see routing.Routing#hops(models.Point, models.Point)
	 */
	@Override
	public int hops(Point start, Point end) {
		// TODO Auto-generated method stub
		if(start.equals(end))
			return 0;
		boolean startI = parkingLot.search(start) == Map.I,
				startE = parkingLot.search(start) == Map.E,
				startP = parkingLot.search(start) >= 0,
				endI = parkingLot.search(end) == Map.I,
				endE = parkingLot.search(end) == Map.E,
				endP = parkingLot.search(end) >= 0;
				
		if(startI && endE){//从起点到终点的路径
			return StaticRouting.getHops(start.x, start.y, end.x, end.y, routingI);
		}else if(startE && endI){
			return StaticRouting.getHops(end.x, end.y, start.x, start.y, routingI);
		}else if(startI && endP){
			Point endPInlet = parkingLot.allSpaces.get(parkingLot.search(end)).inlet;
			return StaticRouting.getHops( start.x, start.y,endPInlet.x,endPInlet.y, routingI) + 1;
		}else if(startP && endI){
			Point startPInlet = parkingLot.allSpaces.get(parkingLot.search(start)).inlet;
			return StaticRouting.getHops(end.x, end.y,startPInlet.x, startPInlet.y, routingI) + 1;
		}else if(startE && endP){
			Point endPInlet = parkingLot.allSpaces.get(parkingLot.search(end)).inlet;
			return StaticRouting.getHops(start.x, start.y, endPInlet.x, endPInlet.y,  routingE) + 1;
		}else if(endE && startP){
			Point startPInlet = parkingLot.allSpaces.get(parkingLot.search(start)).inlet;
			return StaticRouting.getHops(end.x, end.y, startPInlet.x, startPInlet.y,  routingE) + 1;
		}else//暂不支持PP之间及起、终点带X的路由
			throw new UnsupportedOperationException();
	}
}
