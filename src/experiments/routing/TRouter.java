/**   
* @Title: TRouter.java 
* @Package experiments.routing 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-06-02
* @version V1.0   
*/
package experiments.routing;

import models.Map;
import models.Router;
import utils.Initiation;

/**
* <p> TRouter</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-06-02
*/
public class TRouter {

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initiation data = new Initiation();	
		
		//输入到控制台
		data.init();
		
		//创建地图
		Map map = new Map(data.map, data.routingIn, data.routingOut);
		
		//利用地图创建一个路由器
		Router router = new Router(map, data.routingIn, data.routingOut);
	
		System.out.println("***测试开始！***");
		System.out.println("打印地图");
		for(int i = 0;i < map.map.length;i ++){
			for(int j = 0;j < map.map[0].length;j ++)
				System.out.printf("%3d", map.map[i][j]);
			System.out.println();
		}
		
		System.out.println("***测试路由（从起点到终点的跳数）***");
		int nSpaces = map.allSpaces.size();
		int randSpaceID = (int) (Math.random() * nSpaces);
		System.out.println("1.起点到终点路由：" + router.hops(map.in, map.out));
		System.out.println("2.终点到起点路由(应该与上一条相同)：" + router.hops(map.out, map.in));
		System.out.println("随机选择一个停车位：" + randSpaceID);
		System.out.println("3.起点到" + randSpaceID+"停车位的路由" + router.hops(map.in, map.allSpaces.get(randSpaceID).location));
		System.out.println( "4.从"+randSpaceID+"停车位到起点的路由（同上）" + router.hops(map.allSpaces.get(randSpaceID).location,map.in));
		System.out.println("5.终点到" + randSpaceID+"停车位的路由" + router.hops(map.out, map.allSpaces.get(randSpaceID).location));
		System.out.println( "6.从"+randSpaceID+"停车位到终点的路由（同上）" + router.hops(map.allSpaces.get(randSpaceID).location,map.out));			
	}

}
