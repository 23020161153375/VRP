/**   
 * @Title: Initiation.java 
 * @Package  
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author FlyingFish
 * @date 2017-05-17
 * @version V1.0   
 */
package utils;
import java.util.Scanner;

import models.Map;
import models.VRP;
import models.Point;
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

	public int fRobots;			//机器人系数
	public int fWaiting;		//等待系数
	public int fPanishment;		//罚时系数
	public int fEnergy;			//能耗系数
	public int high;			//高
	public int width;			//低
	public int carNumber;		//车辆数目

	public VRP[] applications;	//VRP
	public Point inner;
	public Point outer;
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
		init.init();

	}

	public void init() {

		// String fileInPath = "h:/fileRead.txt";
		// File f = new File(fileInPath);
		// Scanner cin = new Scanner(new FileInputStream(f));

		// 去掉前面的文件流 把cin改成控制台输入
		Scanner cin = new Scanner(System.in);

		fEnergy = cin.nextInt(); // 能耗系数
		fPanishment = cin.nextInt(); // 罚时系数
		fRobots = cin.nextInt(); // 机器人系数
		fWaiting = cin.nextInt(); // 等待系数
		high = cin.nextInt(); // 高
		width = cin.nextInt(); // 宽
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
					if(i != 0 && i != high - 1 && j != 0 && j != width - 1){		//判断出口是否在地图边缘
						System.out.println("NO!");
						System.exit(0);
					}
					output++;
					outer = new Point(i,j);
					map[i][j] = Map.E;
				} else if (temp.equals("I")) {
					if(i != 0 && i != high - 1 && j != 0 && j != width - 1){		//判断入口是否在地图边缘
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
			applications[i] = new VRP(temp1, temp2, temp3, temp4, temp5);
		}
		if (!isMapValid()) {	//地图无效
			System.out.println("无效的地图!");
		}
		// String fileOutPath = "h:/fileOut.txt";
		// File systemOut = new File(fileOutPath);
		// systemOut.createNewFile();
		// FileOutputStream fileOutputStream = new FileOutputStream(systemOut);
		// PrintStream printStream = new PrintStream(fileOutputStream);
		//
		// System.setOut(printStream);

		// 去掉对system设置输出流，采用默认的控制台输出，

	}

	public boolean isMapValid() {
		System.out.println("开始判断!");
		for(int i = 0;i < high;i++){
			for(int j = 0;j < width;j++){
				if(map[i][j] == Map.P){							//判断每个红色区域是否旁边有且只有一个白色区域
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
		int [][] xx,yy;
		xx = StaticRouting.routing(inner);
		System.out.println(inner.getX()+" "+inner.getY());
		for(int i = 0;i < high;i++){
			for(int j = 0;j < width;j++){
				System.out.print(xx[i][j]+" ");
			}
			System.out.println();
		}
		yy = StaticRouting.routing(outer);
		return true;
	}
}
