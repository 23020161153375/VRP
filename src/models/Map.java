/**   
* @Title: Case.java 
* @Package models 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package models;


import java.util.Collections;
import java.util.List;

import routing.StaticRouting;



/**
* <p> �����ͼ</p>
* <p>һ��ʼ������ö�ά�����ʾ�ĵ�ͼ��Ϣ����<Code> Map </Code>�ж���ĳ������ʾ��
* ��û�ж�ͣ��λ��ţ�ȫ��Ϊ{@link #P}��������������̬·�ɶ�ͣ��λ������ھ��ó�λ�����ڵ���̾���ĳ��̱�ţ���ʱ��Ӧ�����λ�ô�ŵ��ǳ�λ��š� </p>
* <p>����ĵ�ͼӦ�����Ѿ��ж���Ч�ġ�</p>
* @author FlyingFish
* @date 2017-05-20
*/
public class Map{
	/** 
	* @Fields P : TODO(ͣ��λ) 
	*/ 
	public static final int P = 0;
	
	/** 
	* @Fields E : TODO(����) 
	*/ 
	public static final int E = -1;
	
	/** 
	* @Fields I : TODO(���) 
	*/ 
	public static final int I = -2;
	
	/** 
	* @Fields B : TODO(�ϰ�) 
	*/ 
	public static final int B = -3;
	
	/** 
	* @Fields X : TODO(ͨ��) 
	*/ 
	public static final int X = -4;
	
	public int[][] map;
	
	//ͣ��λ
	public List<ParkingSpace> allSpaces;
	
	//��ڡ�����
	public Point in,out;
	
	public Map(int[][] map,int[][] routingI,int[][] routingE){
		this.map = map;
		init();
		orderParkingSpace(routingI,routingE);
	}
	
	/** 
	* <p>���ض�Ӧλ�õ���Ϣ������ </p> 
	* <p>Description: </p> 
	* @param p
	* @return 
	*/
	public int search(Point p){
		return map[p.x][p.y];
	}
	
	/** 
	* <p>��ʼ�� </p> 
	* <p>��ʼ����ʱ���������ŵ� </p>  
	*/
	private void init(){
		int countPS = 0;
		for(int i = 0;i < map.length;i ++)
			for(int j = 0;j < map[0].length;j ++){
				if(map[i][j] == Map.P){
					//ͣ��λ���
					map[i][j] = countPS;
					Point ps = new Point(i,j);
					Point inlet = getInletOfParkingSpace(ps);
					allSpaces.add(new ParkingSpace(countPS++,ps,inlet));
				}else if(map[i][j] == Map.E)
					//ע��X��Y��ķ���
					out = new Point(i,j);
				else if(map[i][j] == Map.I)
					in = new Point(i,j);
			}
	}
	
	//��ĳ��ͣ��λ�����
	private Point getInletOfParkingSpace(Point psLoc){
		int xOffset = 0,yOffset = 0;
		if(map[psLoc.x +1][psLoc.y] == Map.X)
			xOffset = 1;
		else if(map[psLoc.x][psLoc.y + 1] == Map.X)
			yOffset = 1;
		else if(map[psLoc.x - 1][psLoc.y] == Map.X)
			xOffset = -1;
		else 
			yOffset = -1;
			
		return new Point(psLoc.x + xOffset,psLoc.y + yOffset);
	}
	
	//�Ե�ͼ��ͣ��λ���±�ţ�ʹ�䰴·����������
	private  void orderParkingSpace(int[][] routingI,int[][] routingE){
		for(int i = 0;i < allSpaces.size();i ++){
			ParkingSpace ps = allSpaces.get(i);
			allSpaces.get(i).updateKey(
					StaticRouting.getHops(in.x, in.y, ps.location.x, ps.location.y, routingI)
					+ StaticRouting.getHops(out.x,out.y,ps.location.x,ps.location.y,routingE));
		}
		
		//ͣ��λ����
		Collections.sort(allSpaces);
		for(int i = 0; i < allSpaces.size();i ++){
			ParkingSpace space = allSpaces.get(i);
			space.id = i;
			map[space.location.x][space.location.y] = i;
		}		
	}
}