/**
 * @Title: Initiation.java
 * @Package
 * @Description: TODO(��һ�仰�������ļ���ʲô)
 * @author FlyingFish
 * @date 2017-05-17
 * @version V1.0
 */
package utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import models.Map;
import models.Point;
import models.VRP;
import routing.StaticRouting;
/**
 * <p>
 * Initiation
 * </p>
 * <p>
 * Description:
 * </p>
 *
 * @author FlyingFish
 * @date 2017-05-17
 */
public class Initiation {

	public int[][] map;

	public int fRobots;			//������ϵ��
	public int fWaiting;		//�ȴ�ϵ��
	public int fPanishment;		//��ʱϵ��
	public int fEnergy;			//�ܺ�ϵ��
	public int high;			//��
	public int width;			//��
	public int carNumber;		//������Ŀ

	public VRP[] applications;	//VRP
	public Point inner;
	public Point outer;

	public int[][] routingIn,routingOut;
	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Initiation init = new Initiation();
		Scanner cin = new Scanner(System.in);
		init.init(cin);

	}

	public void init(Scanner cin) {

		// String fileInPath = "h:/fileRead.txt";
		// File f = new File(fileInPath);
		// Scanner cin = new Scanner(new FileInputStream(f));

		// ȥ��ǰ����ļ��� ��cin�ĳɿ���̨����
		
		fEnergy = cin.nextInt(); // �ܺ�ϵ��
		fPanishment = cin.nextInt(); // ��ʱϵ��
		fRobots = cin.nextInt(); // ������ϵ��
		fWaiting = cin.nextInt(); // �ȴ�ϵ��
		high = cin.nextInt(); // ��
		width = cin.nextInt(); // ��
		map = new int[high][width];
		System.out.println(fEnergy + " " + fPanishment + " " + fRobots + " "
				+ fWaiting + " " + width + " " + high);
		String temp;
		int input = 0,output = 0;
		for (int i = 0; i < high; i++) {
			for (int j = 0; j < width; j++) {
				temp = cin.next();
				if (temp.equals("X")) {
					map[i][j] = Map.X;
				} else if (temp.equals("B")) {
					map[i][j] = Map.B;
				} else if (temp.equals("E")) {
					if(i != 0 && i != high - 1 && j != 0 && j != width - 1){		//�жϳ����Ƿ��ڵ�ͼ��Ե
						System.out.println("NO!");
						System.exit(0);
					}
					output++;
					outer = new Point(i,j);
					map[i][j] = Map.E;
				} else if (temp.equals("I")) {
					if(i != 0 && i != high - 1 && j != 0 && j != width - 1){		//�ж�����Ƿ��ڵ�ͼ��Ե
						System.out.println("NO!");
						System.exit(0);
					}
					input++;
					inner = new Point(i,j);
					map[i][j] = Map.I;
				} else {
					map[i][j] = Map.P;
				}
				System.out.print(map[i][j] + " ");
			}
			System.out.println();
		}
		if(input != 1||output != 1){
			System.out.println("NO!");
			System.exit(0);
		}
		carNumber = cin.nextInt();
		applications = new VRP[carNumber];
		for (int i = 0; i < carNumber; i++) {
			int temp1 = cin.nextInt();
			int temp2 = cin.nextInt();
			int temp3 = cin.nextInt();
			int temp4 = cin.nextInt();
			int temp5 = cin.nextInt();
			
			applications[i] = new VRP(temp1 - 1, temp2, temp3, temp4, temp5);
		}
		if (!isMapValid()) {	//��ͼ��Ч
			System.out.println("��Ч�ĵ�ͼ!");
			System.exit(0);
		}
		System.out.println("��ʼ���ɹ�");
	}

	public boolean isMapValid() {
		System.out.println("��ʼ�ж�!");
		for(int i = 0;i < high;i++){
			for(int j = 0;j < width;j++){
				if(map[i][j] == Map.P){							//�ж�ÿ����ɫ�����Ƿ��Ա�����ֻ��һ����ɫ����
					int Xnum = 0;
					if(i < high - 1 && map[i+1][j] == Map.X)
						Xnum++;
					if(i > 0 && map[i-1][j] == Map.X)
						Xnum++;
					if(j < width - 1 && map[i][j+1] == Map.X)
						Xnum++;
					if(j > 0 && map[i][j-1] == Map.X)
						Xnum++;
					if(Xnum != 1){
						System.out.println("NO!");
						return false;
					}
					//System.out.print(Xnum + " ");
				}
			}
			//System.out.println();
		}
		StaticRouting.init(map);
		routingIn = StaticRouting.routing(inner);
		System.out.println(inner.getX()+" "+inner.getY());
		for(int i = 0;i < high;i++){
			for(int j = 0;j < width;j++){
				if(map[i][j] == Map.P){
					int judge = 0;
					if(i < high - 1 && routingIn[i+1][j] != 0)
						judge++;
					if(i > 0 && routingIn[i-1][j] != 0)
						judge++;
					if(j < width - 1 && routingIn[i][j+1] != 0)
						judge++;
					if(j > 0 && routingIn[i][j-1] != 0)
						judge++;
					if(judge != 1){
						System.out.println("��P���ɴ�");
						return false;
					}
				}
			}
		}
		System.out.println("��ͼ����������");
		StaticRouting.init(map);//һ��Ҫ�ָ�ͼ
		routingOut = StaticRouting.routing(outer);
		return true;
	}
}