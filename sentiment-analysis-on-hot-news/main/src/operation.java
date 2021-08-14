import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class operation {

	public static void main(String[] args) throws Exception{
	

		getdata apple=new getdata();
		apple.setarg("http://www.appledaily.com.tw/rss/newcreate/kind/rnews/type/hot", "title","link", 2, 2);
		
		//apple.run();
		//apple.parsecomment();
	   
		//apple.segment2();
	//apple.runudnhot();//已完成
	//apple.runsetnhot();//已完成     1     
	//	apple.runettodayhot();//已完成1
		//apple.runltnhot();//已完成        1 
		//apple.runposapple();//已完成  1 
		//apple.getfws();
		//apple.getcommentformat(apple.getfws());
		System.out.println(apple.homogeneity("張善政", "2016-04-27"));
		
	
	 //留言友達限制，但因被辨別為無效，導致list_news有而分類沒有
		
		

		
		
		                                                     }
	

}
