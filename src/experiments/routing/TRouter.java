/**   
* @Title: TRouter.java 
* @Package experiments.routing 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
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
		
		//���뵽����̨
		data.init();
		
		//������ͼ
		Map map = new Map(data.map, data.routingIn, data.routingOut);
		
		//���õ�ͼ����һ��·����
		Router router = new Router(map, data.routingIn, data.routingOut);
	
		System.out.println("***���Կ�ʼ��***");
		System.out.println("��ӡ��ͼ");
		for(int i = 0;i < map.map.length;i ++){
			for(int j = 0;j < map.map[0].length;j ++)
				System.out.printf("%3d", map.map[i][j]);
			System.out.println();
		}
		
		System.out.println("***����·�ɣ�����㵽�յ��������***");
		int nSpaces = map.allSpaces.size();
		int randSpaceID = (int) (Math.random() * nSpaces);
		System.out.println("1.��㵽�յ�·�ɣ�" + router.hops(map.in, map.out));
		System.out.println("2.�յ㵽���·��(Ӧ������һ����ͬ)��" + router.hops(map.out, map.in));
		System.out.println("���ѡ��һ��ͣ��λ��" + randSpaceID);
		System.out.println("3.��㵽" + randSpaceID+"ͣ��λ��·��" + router.hops(map.in, map.allSpaces.get(randSpaceID).location));
		System.out.println( "4.��"+randSpaceID+"ͣ��λ������·�ɣ�ͬ�ϣ�" + router.hops(map.allSpaces.get(randSpaceID).location,map.in));
		System.out.println("5.�յ㵽" + randSpaceID+"ͣ��λ��·��" + router.hops(map.out, map.allSpaces.get(randSpaceID).location));
		System.out.println( "6.��"+randSpaceID+"ͣ��λ���յ��·�ɣ�ͬ�ϣ�" + router.hops(map.allSpaces.get(randSpaceID).location,map.out));			
	}

}
