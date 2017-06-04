/**   
* @Title: Case.java 
* @Package models 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package models;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import routing.StaticRouting;
import models.Router;
import utils.Initiation;


/**
* <p> 车库地图</p>
* <p>一开始输入的用二维数组表示的地图信息是用<Code> Map </Code>中定义的常量组表示的
* 但没有对停车位编号（全部为{@link #P}），这里会借助静态路由对停车位按从入口经该车位带出口的最短距离的长短编号，此时对应数组的位置存放的是车位编号。 </p>
* <p>输入的地图应该是已经判断有效的。</p>
* @author FlyingFish
* @date 2017-05-20
*/
public class Map{
	/** 
	* @Fields P : TODO(停车位) 
	*/ 
	public static final int P = 0;
	
	/** 
	* @Fields E : TODO(出口) 
	*/ 
	public static final int E = -1;
	
	/** 
	* @Fields I : TODO(入口) 
	*/ 
	public static final int I = -2;
	
	/** 
	* @Fields B : TODO(障碍) 
	*/ 
	public static final int B = -3;
	
	/** 
	* @Fields X : TODO(通道) 
	*/ 
	public static final int X = -4;
	
	public int[][] map;
	
	//停车位
	public List<ParkingSpace> allSpaces;
	
	//入口、出口
	public Point in,out;
	
	public Map(int[][] map,int[][] routingI,int[][] routingE){
		this.map = map;
		allSpaces=new ArrayList<ParkingSpace>();
		init();
		orderParkingSpace(routingI,routingE);
	}
	
	/** 
	* <p>返回对应位置的信息，参数 </p> 
	* <p>Description: </p> 
	* @param p
	* @return 
	*/


	public int search(Point p){
		return map[p.x][p.y];
	}
	
	/** 
	* <p>初始化 </p> 
	* <p>初始化的时候是随意编号的 </p>  
	*/
	private void init(){
		int countPS = 0;
		for(int i = 0;i < map.length;i ++)
			for(int j = 0;j < map[0].length;j ++){
				if(map[i][j] == Map.P){
					//停车位编号
					map[i][j] = countPS;
					Point ps = new Point(i,j);
					Point inlet = getInletOfParkingSpace(ps);
					ParkingSpace test=new ParkingSpace(countPS,ps,inlet);
					allSpaces.add(test);
					countPS++;
				}else if(map[i][j] == Map.E)
					//注意X轴Y轴的方向
					out = new Point(i,j);
				else if(map[i][j] == Map.I)
					in = new Point(i,j);
			}
	}
	
	//找某个停车位的入口
	private Point getInletOfParkingSpace(Point psLoc){
		int n = map.length;
		int m = map[0].length;
		int xOffset = 0,yOffset = 0;
		if(psLoc.x+1<n&&map[psLoc.x +1][psLoc.y] == Map.X)
			xOffset = 1;
		else if(psLoc.y+1<m&&map[psLoc.x][psLoc.y + 1] == Map.X)
			yOffset = 1;
		else if(psLoc.x>0&&map[psLoc.x - 1][psLoc.y] == Map.X)
			xOffset = -1;
		else 
			yOffset = -1;
			
		return new Point(psLoc.x + xOffset,psLoc.y + yOffset);
	}
	
	//对地图的停车位重新编号，使其按路径距离重排
	private  void orderParkingSpace(int[][] routingI,int[][] routingE){
		for(int i = 0;i < allSpaces.size();i ++){
			ParkingSpace ps = allSpaces.get(i);
			allSpaces.get(i).updateKey(
					StaticRouting.getHops(in.x, in.y, ps.inlet.x, ps.inlet.y, routingI)
					+ StaticRouting.getHops(out.x,out.y,ps.inlet.x,ps.inlet.y,routingE)+2);

		}
		
		//停车位排序
		Collections.sort(allSpaces);
		for(int i = 0; i < allSpaces.size();i ++){
			ParkingSpace space = allSpaces.get(i);
			space.id = i;
			map[space.location.x][space.location.y] = i;
		}		
	}
	
	/** 
	* <p>打印地图 </p> 
	* <p>Description: </p>  
	*/
	public void print(){
		for(int i = 0;i < map.length;i ++){
			for(int j = 0;j < map[0].length;j ++)
				System.out.printf("%3d", map[i][j]);
			System.out.println();
		}
	}
}
