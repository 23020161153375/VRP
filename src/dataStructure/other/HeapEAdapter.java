/**   
* @Title: HeapInteger.java 
* @Package dataStructureSupport 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-04-22
* @version V1.0   
*/
package dataStructure.other;

/**
* <p> HeapEAdapter</p>
* <p>һ�����Ķ�Ԫ����������ʵ����<Code>HeapElement</Code>�ӿڣ�Ԫ�ر�����Ϊ<Code>Comparable</Code>�������࣬�����ܼ���ֵͳһ��������Է���ʵ����ԡ�
* </p>
* @author FlyingFish
* @date 2017-04-22
*/
public class HeapEAdapter<E extends Comparable<? super E>> implements HeapElement<E>,Comparable<HeapEAdapter<E>> {
	E value;
	int loc;
		
	protected HeapEAdapter(E value){
		this.value = value;
	}
		
	public static<T extends Comparable<? super T>> HeapEAdapter<T>[] getAdapters(T[] elems){
		HeapEAdapter<T>[] adps = new HeapEAdapter[elems.length];
		
		for(int i = 0; i < elems.length;i ++)
			adps[i] = new HeapEAdapter<T>(elems[i]);
		
		return adps;	
	}
	
	public int compareTo(HeapEAdapter<E> e){
		Comparable<? super E> key =( Comparable<? super E>) value;
		return key.compareTo(e.value);		
	}
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/** (non-Javadoc)
	 * @see dataStructure.HeapElement#setElementLocation(int)
	 */
	@Override
	public void setElementLocation(int location) {
		// TODO Auto-generated method stub
		loc = location;
	}

	/** (non-Javadoc)
	 * @see dataStructure.HeapElement#getElementLocation()
	 */
	@Override
	public int getElementLocation() {
		// TODO Auto-generated method stub
		return loc;
	}

	public String toString(){
		return value.toString() + "/" + loc;
	}

	/** (non-Javadoc)
	 * @see dataStructure.HeapElement#updateKey(java.lang.Object)
	 */
	@Override
	public void updateKey(E key) {
		// TODO Auto-generated method stub
		//����ֵ�����Ǽ�
		value = key;
	}
}
