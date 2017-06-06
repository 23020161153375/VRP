/**   
* @Title: VRP.java 
* @Package models 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package models;

/**
* <p> VRP</p>
* <p> </p>
* @author FlyingFish
* @date 2017-05-20
*/
public class VRP {
// Vehicle Routing Planning	

	public int carID;
	
	//�������ʱ��
	public int requestTime;
	
	//���ȴ�ʱ��
	public int longestWatingTime;
	
	//�������ʱ��
	public int pullOutTime;
	
	//��������
	public int carMass;
	
	/** 
	* <p>���캯�� </p> 
	* <p>Description: </p> 
	* @param id ע�⣬Ϊ����ʹ�ã����ڲ�������Ŵ�0��ʼ
	* @param rt
	* @param lwt
	* @param pot
	* @param mass 
	*/
	public VRP(int id,int rt,int lwt,int pot,int mass){
		carID = id;
		requestTime = rt;
		longestWatingTime = lwt;
		pullOutTime = pot;
		carMass = mass;
	}
	
	/** 
	* @Fields refused : TODO(�Ƿ����) 
	*/ 
	public boolean refused;
	
	//ÿ�����ܵ������Ӧ�����(task1)���ͳ�������(task2)
	public Task task1,task2;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
