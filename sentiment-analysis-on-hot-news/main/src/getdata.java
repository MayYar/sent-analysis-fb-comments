import java.net.*;                
import java.io.*;

import main.similarcheck;

import org.dom4j.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;  
import facebook4j.auth.AccessToken;
import facebook4j.internal.org.json.JSONArray; 
import facebook4j.internal.org.json.JSONObject;
import tw.cheyingwu.ckip.CKIP; 
import tw.cheyingwu.ckip.Term;

import java.util.HashMap;  
import java.io.FileReader;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Scanner;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.io.BufferedReader;    
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;






import facebook4j.internal.org.json.JSONException;

 
//注意若留言的斷詞並沒有在維度中出現，則txt檔不顯示且出現下行 流言之斷詞
//使用前先重新train一次model才不會出錯
//1209:仍需解決將資料庫中的每則留言給予電腦判斷結果
public class getdata 
{
	public String URL;       //新聞RSS網址
	public String titletag;  //該新聞標題的tag
	public String webtag;    //該新聞網址的網址
	public String token="CAACEdEose0cBAI0gi1B865nKrKVlYuoPiNMgQL7xuEX0bgNT2spJQiG20kHvzrQo1tDPZBnMNK9ipZAmw81r7UNTNon6jeDGztOAgBaDY3ZCdxCLapMHVRYnPHfvT5EmspbJHqdLvBSmKQmRp0r7Hs6CYcEtZAW3cFxLAecZBPQUrDYTM8KzI7sJ2yTYiZBQrgdss9bBoF4LZAoXJZCrmpzZA";
	public String golcomment;
	public int titlediff;    //輸入新聞標題的index(通常前面的tag為無關緊要的文字ex:新聞公司名稱等...，所以爬取第一個真正新聞標提之index)
	public int webdiff;     //輸入新聞網址的index
	
	
	public void setarg(String newURL,String newtitletag,String newwebtag,int newtitlediff,int newwebdiff)
	{
		URL=newURL;
		titletag=newtitletag;
		webtag=newwebtag;
		titlediff=newtitlediff;
		webdiff=newwebdiff;    //將以上參數存入變數
		
	}
	public void run() throws IOException,DocumentException,Exception
	{
		int time=0;//用於迴圈作為計數
		URL url;//jsoup中提供的CLASS
		url = new URL(URL);//藍字URL為新聞RSS
		Document xmlDoc =  Jsoup.parse(url, 3000);//使用Jsoup jar 去解析網頁
		
		Elements title = xmlDoc.select(titletag); //要解析的tag元素為title
		Elements link = xmlDoc.select(webtag);//解析title連結
		Facebook facebook = new FacebookFactory().getInstance();//以下4行程式碼作為存取FQL的權限(用來取得FB留言)
		facebook.setOAuthAppId("0","0");                            
		facebook.setOAuthPermissions("0");
		facebook.setOAuthAccessToken(new AccessToken("0", null));
//-----------------------------------------------------------------------------
		
		
		
		for(time=1;time<30;time++)//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
			FileWriter out = new FileWriter("allinstring.txt",false);//將留言全部合在一起成為一個string
			
			if(title.get(time+titlediff).text().isEmpty()||link.get(time+webdiff).text().isEmpty()||title.get(time+titlediff).text().substring(0, 7).equals("《TAIPEI"))
			break;//由於ltn新聞網的RSS最後幾則是提供國際英文的新聞，故當掃到新聞標題為英文時就停止爬取
			
			
			System.out.println("--------------《"+title.get(time+titlediff).text()+"》--------------");//新聞標題
			JSONArray jsonArray2 =new JSONArray();//facebook4j所提供的class
			if(link.get(time+webdiff).text().substring(7, 28).equals("www.appledaily.com.tw"))
			{
				//由於爬取蘋果新聞的網址時最後一個符號會多擷取一個"/"，所以僅擷取第0到70的字元作為網址
				 jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='"+link.get(time+webdiff).text().substring(0, 70)+"'");
			}
			else
			{
			   jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='"+link.get(time+webdiff).text()+"'");
			   //爬取其他新聞的網址則沒有此問題
			}
			JSONObject jsonObject2 = jsonArray2.getJSONObject(0);//用來取得該留言板的ID
			String query = "SELECT text FROM comment WHERE object_id='"+jsonObject2.get("comments_fbid")+"'";
			JSONArray jsonArray = facebook.executeFQL(query);//將ID及抓取留言版的語法輸入至facebook FQL執行取得
			
			FileWriter outrestore=new FileWriter("afterrestore.txt",true);
			//outrestore.write("--------------《"+title.get(time+titlediff).text()+"》--------------");
			//outrestore.write("\r\n");
			//outrestore.close();
			String s;
			if(jsonArray.length()==0)
			{
				continue;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject jsonObject = jsonArray.getJSONObject(i);
			    s=jsonObject.get("text").toString().replaceAll("\\s+","");
			    s=s.toString().replaceAll("\"+","'" );
			    s=s.toString().replaceAll("、","." );
			    if(s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			   // System.out.println(s);
			    out.write("、");
			    out.write(s);
			    
			}
			out.close();
			
			FileReader fr = new FileReader("allinstring.txt");
			BufferedReader br = new BufferedReader(fr);
			 String content;
			 content=br.readLine().toString();//將此string丟入CKIP
			 HttpCKIP HttpCKIP = new HttpCKIP();
			 Content a=new Content(HttpCKIP.getCKIPByHTTP(content));//輸出afterrestore.txt(已還原一則一則留言(已斷詞過))
			 
		
		}	
	}
	public ArrayList<combination> getfws() throws Exception
	{
		 int N;
			ArrayList<String> posWord = new ArrayList<String>();//正面類別所有詞彙
			ArrayList<String> posnotRp = new ArrayList<String>();//正面類別不重複所有詞彙
			ArrayList<Integer> posNRNum = new ArrayList<Integer>();//不重複各詞彙個數
			
			ArrayList<String> negWord = new ArrayList<String>();//負面類別所有詞彙
			ArrayList<String> negnotRp = new ArrayList<String>();//負面類別不重複所有詞彙
			ArrayList<Integer> negNRNum = new ArrayList<Integer>();//負面不重複各詞彙個數
			
			//----------POSITIVE---------------------------------------------------	
			FileReader frpos = new FileReader("pospart.txt");
			BufferedReader brpos = new BufferedReader(frpos);
			
			while(brpos.ready()){
				String str = brpos.readLine();
				String[] splitStr = str.split(", ");
				for (int i = 0; i < splitStr.length; i++){
					//System.out.println(splitStr[i]);
					posWord.add(splitStr[i]);
				}
				
			}
		//	System.out.println("c(positive) = "+ posWord.size());//c(e) e:positive
			frpos.close();
			
			int posWordnum;
			for(int i =0;i<posWord.size();i++){
				posWordnum = 0;
				for(int j = 0;j<posWord.size();j++){
					if(posWord.get(i).equals(posWord.get(j))){
						posWordnum++;
					}
				}
				if(posnotRp.contains(posWord.get(i)))
					continue;
				else{
					posnotRp.add(posWord.get(i));
					posNRNum.add(posWordnum);
				}
			}
			for(int i =0;i<posnotRp.size();i++){
			//	System.out.println(posnotRp.get(i)+"...出現次數: "+posNRNum.get(i));
			}
			
			//----------NEGATIVE---------------------------------------------------		
			FileReader frneg = new FileReader("negpart.txt");
			BufferedReader brneg = new BufferedReader(frneg);
		
				while(brneg.ready()){
					String str = brneg.readLine();
					String[] splitStr = str.split(", ");
					for (int i = 0; i < splitStr.length; i++){
						//System.out.println(splitStr[i]);
						negWord.add(splitStr[i]);
					}
			
				}
			//	System.out.println("c(negative) = "+ negWord.size());//c(e) e:negative
				frneg.close();
				
				int negWordnum;
				for(int i =0;i<negWord.size();i++){
					negWordnum = 0;
					for(int j = 0;j<negWord.size();j++){
						if(negWord.get(i).equals(negWord.get(j))){
							negWordnum++;
						}
					}
					if(negnotRp.contains(negWord.get(i)))
						continue;
					else{
						negnotRp.add(negWord.get(i));
						negNRNum.add(negWordnum);
					}
				}
			for(int i =0;i<negnotRp.size();i++){
				//	System.out.println(negnotRp.get(i)+"...出現次數: "+negNRNum.get(i));
				}
				
				N = posWord.size() + negWord.size();
				//System.out.println("N = "+ N); //語料全部詞彙總數
				
	//-----------------處理重複----------------------------------------------------------			
				ArrayList<String> TotalnotRp = new ArrayList<String>();//全部不重複所有詞彙
				ArrayList<Integer> TotalNRNum = new ArrayList<Integer>();//全部不重複各詞彙個數
				//判斷重複並加入全部詞彙集
				int plus ;
				for(int i = 0;i<posnotRp.size();i++){
					for(int j = 0;j<negnotRp.size();j++){
						if(posnotRp.get(i).equals(negnotRp.get(j))){
							TotalnotRp.add(posnotRp.get(i));
							plus = posNRNum.get(i) + negNRNum.get(j);
							TotalNRNum.add(plus);
						}
					}
				}
				
				//把沒有重複的詞加入		
				for(int i = 0;i<posnotRp.size();i++){
					if(TotalnotRp.contains(posnotRp.get(i))==false){
						TotalnotRp.add(posnotRp.get(i));
					TotalNRNum.add(posNRNum.get(i));
					}
				}
				for(int i = 0;i<negnotRp.size();i++){
					if(TotalnotRp.contains(negnotRp.get(i))==false){
						TotalnotRp.add(negnotRp.get(i));
						TotalNRNum.add(negNRNum.get(i));
					}
					
				}
				//顯示全部詞彙個數(沒有重複)
				
				
				
				
				
		//------------取得positive之每個留言的PMI-----------------------------------------------------
	           combination[] unit = new combination[2*TotalnotRp.size()];
				
				for(int w =0;w<TotalnotRp.size();w++){
					unit[2*w]=new combination();
					unit[2*w].word=TotalnotRp.get(w);
					unit[2*w].tag="Positive";
					
					unit[(2*w)+1]=new combination();
					unit[(2*w)+1].word=TotalnotRp.get(w);
					unit[(2*w)+1].tag="Negative";
				}
				
				FileReader fgetacomment = new FileReader("pospart.txt");
				BufferedReader bgetacomment = new BufferedReader(fgetacomment);
				String acomment;
				String[] acommentsplit;
				
				double Co=0,Comax=-10000,Comin=10000;
			//	System.out.println("-----------------正面情緒---------------------");
			while(bgetacomment.ready())
			{	
			/*   if(count==5)//只取前4則留言，若想跑全部留言，這此if可刪除
			   {
				break;
			   }*/
				acomment = bgetacomment.readLine();
				acommentsplit = acomment.split(", ");	
			//	System.out.println("第"+count+"句:"+acomment);
				for (int i = 0; i <acommentsplit.length; i++)
				{
					for(int h =0;h<posnotRp.size();h++)
					{
						if(posnotRp.get(h).equals(acommentsplit[i]))
						{
							for(int t =0;t<TotalnotRp.size();t++)
							{
								if(TotalnotRp.get(t).equals(acommentsplit[i]))
								{
									
									Co=posNRNum.get(h)*(Math.log((posNRNum.get(h)*N)/((posWord.size()*TotalNRNum.get(t))*1.00))/Math.log(2));
									if(Co>Comax)
									{
										Comax=Co;
									}
									if(Co<Comin)
									{
										Comin=Co;
									}
							//		System.out.print("Co(positive,"+posnotRp.get(h)+")=");
							//		System.out.printf("%.2f\n",Co);
									for(int a=0;a<2*TotalnotRp.size();a++)
									{
										if(unit[a].word.equals(posnotRp.get(h))&&unit[a].tag.equals("Positive"))
										{
											unit[a].pmi=Co;
										}
										
									}
								}
								
								
							}
							
						}
						
					}
					
				}
			}
				fgetacomment.close();
	//----------------------------------------取negative之每個留言的PMI-----------------------------------------------------
				FileReader fgetacomment2 = new FileReader("negpart.txt");
				BufferedReader bgetacomment2 = new BufferedReader(fgetacomment2);

				String acomment2 ;
				String[] acommentsplit2;
				
				double Co2;
				
			//	System.out.println("-----------------負面情緒---------------------");
				
			while(bgetacomment2.ready())
			{
				
			/*	if(count2==5)//只取前4則留言，若想跑全部留言，這此if可刪除
				{
					break;
				}*/
				acomment2 = bgetacomment2.readLine();
				acommentsplit2 = acomment2.split(", ");
			//	System.out.println("第"+count2+"句:"+acomment2);
				for (int i = 0; i <acommentsplit2.length; i++)
				{
					for(int h =0;h<negnotRp.size();h++)
					{
						if(negnotRp.get(h).equals(acommentsplit2[i]))
						{
							for(int t =0;t<TotalnotRp.size();t++)
							{
								if(TotalnotRp.get(t).equals(acommentsplit2[i]))
								{
									Co2=negNRNum.get(h)*(Math.log((negNRNum.get(h)*N)/((negWord.size()*TotalNRNum.get(t))*1.00))/Math.log(2));
									if(Co2>Comax)
									{
										Comax=Co2;
									}
									if(Co2<Comin)
									{
										Comin=Co2;
									}
					//			System.out.print("Co(negative,"+negnotRp.get(h)+")=");
					//				System.out.printf("%.2f\n",Co2);
									for(int a=0;a<2*TotalnotRp.size();a++)
									{
										if(unit[a].word.equals(negnotRp.get(h))&&unit[a].tag.equals("Negative"))
										{
											unit[a].pmi=Co2;
										}
										
									}
								}
								
								
							}
							
						}
						
					}
					
				}
			}
				fgetacomment2.close();
	//---------------------------------------特徵選擇演算法------------------------------
				combination temp=new combination();
				int FN=(2*TotalnotRp.size())/8,fc=0;
				int checkstate=0;
				double T=0.62;
				ArrayList<combination> FWS = new ArrayList<combination>();
				
				for(int e=0;e<2*TotalnotRp.size();e++)                       //正規化
				{
					unit[e].pmi=(unit[e].pmi-Comin)/((Comax-Comin)*1.00);
					
				}
				for(int e=0;e<2*TotalnotRp.size();e++)               //先降冪排序
				{
					for(int f=0;f<2*TotalnotRp.size()-1;f++)
					{
						if (unit[f].pmi < unit[f+1].pmi)
						{
							temp = unit[f+1];
							unit[f+1] = unit[f];
							unit[f] = temp;

						}
					}
					
				}
			/*	for(int e=0;e<2*TotalnotRp.size();e++)
				{
					System.out.println(unit[e].word+"  "+unit[e].tag+"  "+unit[e].pmi);
				}*/
				FWS.add(unit[0]);
				fc=1;
				for(int e=1;e<2*TotalnotRp.size();e++)
				{
					for(int g=0;g<fc;g++)
					{
						if(unit[e].word.equals(FWS.get(g).word))
						{
							if((FWS.get(g).pmi-unit[e].pmi)<=T)
							{
								FWS.remove(g);
								fc--;
								checkstate=1;
								break;
							}
						}
					}
					if(checkstate==0)
					{
						FWS.add(unit[e]);
						fc++;
					}
					checkstate=0;
					if(fc==FN)
					{
						break;
					}
					
				}
				//System.out.println("FWS內共有:"+FWS.size()+"個關鍵詞");//<arraylist> FWS為篩選出來的關鍵詞集合,  arraylist的type為combination
				return FWS;
	}
	public void getcommentformat(ArrayList<combination> FWS) throws Exception
	{
		 FileReader frtrain = new FileReader("commentckip.txt");//要做training的文件，在此以negpart.txt做範例
			BufferedReader brtrain = new BufferedReader(frtrain);
			FileWriter training=new  FileWriter("format.txt",false);
			
			String oricomment ;
			String[] splitoricomment;
			String oritree ;
			String[] splitoritree ;
			
			ArrayList<combination> infwsword = new ArrayList<combination>();
			int stopwhile=0;
			
			
			while(brtrain.ready())                                //掃描negpart.txt中的每一句
			{
				int [] vector = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
				oricomment = brtrain.readLine();
				splitoricomment = oricomment.split(", ");      //將此句的詞存入splitoricomment
				for (int i = 0; i < splitoricomment.length; i++)     //將詞一個一個跟FWS(關鍵詞集合)做比對，若這詞有在FWS，則將此詞另外放入infwsword
				{
					for(int e1=0;e1<FWS.size();e1++)                     
					{
						if(splitoricomment[i].equals(FWS.get(e1).word))
						{
							infwsword.add(FWS.get(e1));
							break;
						}
					}
				}//此時的infwsword內為splitoricomment含有的關鍵詞
				if(infwsword.size()==0)
					continue;
				
				training.write("0 ");
				for(int j1=0;j1<infwsword.size();j1++)         //開始將這些抓出來的關鍵詞做同義詞比對
				{
					int nulltag=1;
					//System.out.print(infwsword.get(j).word+" ");
					stopwhile=0;//別理他，他只用做節省時間，當這關鍵詞找到相對應的詞林時，則停止while，找下一個關鍵詞
					FileReader frtree = new FileReader("sametree.txt");
					BufferedReader brtree = new BufferedReader(frtree);
					while(brtree.ready()&&stopwhile==0)     // 每個關鍵詞都從頭掃過一次詞林
					{
						oritree = brtree.readLine();
						splitoritree = oritree.split(" ");//將詞林以空格做分割存入splitoritree
						
						for(int k=0;k<splitoritree.length;k++)
						{	
							//System.out.println("目前:"+infwsword.get(j).word+" "+splitoritree[k]);
						   if(infwsword.get(j1).word.equals(splitoritree[k]))
						  {
							 nulltag=0; 
							char[] c1 =  splitoritree[0].substring(0,1).toCharArray();
							int n1 = c1[0] - 64;
							//System.out.print(infwsword.get(j).tag.substring(0,1)+"_"+splitoritree[0].substring(0,1)+" ");
							if(infwsword.get(j1).tag.substring(0,1).equals("P"))
								vector[2*n1-2] = 1;
							else
								vector[2*n1-1] = 1;
							stopwhile=1;
							break;
						
						  }
							
						}
						
					}//System.out.print(infwsword.get(j).tag.substring(0,1)+"_null"+" ");
					if(infwsword.get(j1).tag.substring(0,1).equals("P")&&nulltag==1)
						  vector[24] = 1;
					else if(infwsword.get(j1).tag.substring(0,1).equals("N")&&nulltag==1)
						vector[25] = 1;
					
				}
				for(int i =0;i<vector.length;i++){
					if(vector[i] == 1)
					{
					 
					  training.write(i+":"+1+" ");
					}
				}
				
				training.write("\r\n");
				infwsword.clear();
				
			}//br3
			training.close();
	}
	public void runhotapple() throws IOException,DocumentException,Exception
	{
		int time=0;//用於迴圈作為計數
	
		Facebook facebook = new FacebookFactory().getInstance();//以下4行程式碼作為存取FQL的權限(用來取得FB留言)
		facebook.setOAuthAppId("0","0");                            
		facebook.setOAuthPermissions("0");
		facebook.setOAuthAccessToken(new AccessToken("0", null));
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		url = new URL("http://www.appledaily.com.tw/appledaily/hotdaily");//藍字URL為新聞RSS
		Document xmlDoc =  Jsoup.parse(url, 3000);//使用Jsoup jar 去解析網頁	
		
		Elements title = xmlDoc.select("div.aht_title > a[href]");
		

		for (Element e :title) {
		    titles.add(e.attr("title"));
		    urls.add(e.attr("href"));
		}

		System.out.println(titles.get(2));
		System.out.println(urls.get(2).substring(28));
//-----------------------------------------------------------------------------
		
		
		
		for(time=1;time<30;time++)//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
			FileWriter out = new FileWriter("allinstring.txt",false);//將留言全部合在一起成為一個string
			FileWriter outrestore=new FileWriter("afterrestore.txt",true);
			
			
			System.out.println("--------------《"+titles.get(time)+"》--------------");//新聞標題
			//outrestore.write("--------------《"+titles.get(time)+"》--------------");
			//outrestore.write("\r\n");
			JSONArray jsonArray2 =new JSONArray();//facebook4j所提供的class
			
			   jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='http://www.appledaily.com.tw/appledaily/article/headline"+urls.get(time).substring(28,47)+"'");
			   //爬取其他新聞的網址則沒有此問題
			
			JSONObject jsonObject2 = jsonArray2.getJSONObject(0);//用來取得該留言板的ID
			System.out.println(jsonObject2.get("comments_fbid"));
			String query = "SELECT text FROM comment WHERE object_id='"+jsonObject2.get("comments_fbid")+"'";
			JSONArray jsonArray = facebook.executeFQL(query);//將ID及抓取留言版的語法輸入至facebook FQL執行取得
			
			
			//outrestore.write("--------------《"+title.get(time+titlediff).text()+"》--------------");
			//outrestore.write("\r\n");
			//outrestore.close();
			String s;
			if(jsonArray.length()==0)
			{
				continue;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject jsonObject = jsonArray.getJSONObject(i);
			    s=jsonObject.get("text").toString().replaceAll("\\s+","");
			    s=s.toString().replaceAll("\"+","'" );
			    s=s.toString().replaceAll("、","." );
			    if(s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			   // System.out.println(s);
			    out.write("、");
			    out.write(s);
			    
			}
			out.close();
			outrestore.close();
			FileReader fr = new FileReader("allinstring.txt");
			BufferedReader br = new BufferedReader(fr);
			 String content;
			try{
			 content=br.readLine().toString();//將此string丟入CKIP
			
			 HttpCKIP HttpCKIP = new HttpCKIP();
		     Content a=new Content(HttpCKIP.getCKIPByHTTP(content));//輸出afterrestore.txt(已還原一則一則留言(已斷詞過))
			}catch(NullPointerException e)
			{
				continue;
			}
		
		}	
	}
	public int homogeneity(String thisnew,String date)throws Exception
	{
		ArrayList<String> uniterm = new ArrayList<String>();
		ArrayList<String> news = new ArrayList<String>();
		ArrayList<ArrayList<String>> divnews = new ArrayList<ArrayList<String>>();
		similarcheck test = new similarcheck();
		String sample=thisnew;
		
		String allnews="";
		news=test.SelectTable(date);
		for(int i=0;i<news.size();i++)
		{
			allnews=allnews+news.get(i)+"■";	
		}
		//System.out.println(allnews);

		int check=0;
		
		
		    CKIP c = new CKIP("140.109.19.104" , 1501, "text48953", "ieliao");
			
			c.setRawText(allnews);
			c.send();		
			ArrayList<String> tmp = new ArrayList<String>();
			for(Term t : c.getTerm())
			{
				//System.out.println(t.getTerm());	
				if(t.getTerm().equals("？")||t.getTerm().equals("：")||t.getTerm().equals("...")||t.getTerm().equals("！")||t.getTerm().equals("　")||t.getTerm().equals("「")||t.getTerm().equals("」")||t.getTerm().equals("【")||t.getTerm().equals("】")||t.getTerm().equals("／")||"的".equals(t.getTerm())||"是".equals(t.getTerm())||"一".equals(t.getTerm())||"在".equals(t.getTerm())||"有".equals(t.getTerm())
						||"個".equals(t.getTerm())||"我".equals(t.getTerm())||"這".equals(t.getTerm())||"了".equals(t.getTerm())||"他".equals(t.getTerm())||"也".equals(t.getTerm())||"就".equals(t.getTerm())||"人".equals(t.getTerm())||"都".equals(t.getTerm())||"說".equals(t.getTerm())||"而".equals(t.getTerm())||"我們".equals(t.getTerm())||"你".equals(t.getTerm())||"了".equals(t.getTerm())||"要".equals(t.getTerm())
						||"之".equals(t.getTerm())||"及".equals(t.getTerm())||"和".equals(t.getTerm())||"與".equals(t.getTerm())||"以".equals(t.getTerm())||"很".equals(t.getTerm())||"種".equals(t.getTerm())||"中".equals(t.getTerm())||"大".equals(t.getTerm())||"著".equals(t.getTerm())||"她".equals(t.getTerm())||"那".equals(t.getTerm())||"上".equals(t.getTerm())||"但".equals(t.getTerm())||"年".equals(t.getTerm())||"還".equals(t.getTerm())
						||"時".equals(t.getTerm())||"最".equals(t.getTerm())||"自己".equals(t.getTerm())||"為".equals(t.getTerm())||"來".equals(t.getTerm())||"所".equals(t.getTerm())||"他們".equals(t.getTerm())||"兩".equals(t.getTerm())||"各".equals(t.getTerm())||"上".equals(t.getTerm())||"或".equals(t.getTerm())||"等".equals(t.getTerm())||"又".equals(t.getTerm())||"將".equals(t.getTerm())||"因為".equals(t.getTerm())||"於".equals(t.getTerm())
						||"由".equals(t.getTerm())||"從".equals(t.getTerm())||"更".equals(t.getTerm())||"被".equals(t.getTerm())||"才".equals(t.getTerm())||"已".equals(t.getTerm())||"者".equals(t.getTerm())||"每次".equals(t.getTerm())||"把".equals(t.getTerm())||"三".equals(t.getTerm())||"甚麼".equals(t.getTerm())||"其".equals(t.getTerm())||"讓".equals(t.getTerm())||"此".equals(t.getTerm())||"做".equals(t.getTerm())||"在".equals(t.getTerm())
						||"所以".equals(t.getTerm())||"只".equals(t.getTerm())||"則".equals(t.getTerm())||"卻".equals(t.getTerm())||"地".equals(t.getTerm())||"並".equals(t.getTerm())||"位".equals(t.getTerm())||"得".equals(t.getTerm())||"想".equals(t.getTerm())||"去".equals(t.getTerm())||"呢".equals(t.getTerm())||"學生".equals(t.getTerm())||"表示".equals(t.getTerm())||"公司".equals(t.getTerm())||"到".equals(t.getTerm())||"將".equals(t.getTerm())||"不".equals(t.getTerm()))
					continue;
				if(!t.getTerm().equals("■"))
				{	
					tmp.add(t.getTerm());
				}
				else
				{
					System.out.println(tmp);
					divnews.add(tmp);
					tmp = new ArrayList<String>();
					continue;
				}
				for(int i=0;i<uniterm.size();i++)
				{
					if(t.getTerm().equals(uniterm.get(i)))
					{
						check=1;
						   break;
					}
				}
				if(check==0)
				{
					uniterm.add(t.getTerm());
				}
				check=0;
				
		     }

			//System.out.println(divnews);
			
			
			
			int[] vector1=new int[uniterm.size()];
			
			c.setRawText(sample);
			c.send();
			for(Term t : c.getTerm())
			{
				
			   for(int i=0;i<uniterm.size();i++)
				{
					if(t.getTerm().equals(uniterm.get(i)))
					{
						vector1[i]++;
						   break;
					}
				}
				
				
		     }
		for(int j=0;j<news.size();j++)
		{
			int[] vector2=new int[uniterm.size()];
			
			for(int k=0;k<divnews.get(j).size();k++)
			{
				System.out.println(divnews.get(j));
			   for(int i=0;i<uniterm.size();i++)
				{
					if(divnews.get(j).get(k).equals(uniterm.get(i)))
					{
						vector2[i]++;
						   break;
					}
				}
				
				
		     }
		
			double child=0.0;
			double dad=0.0,mom=0.0;
			double result=0.0;
			for(int i=0;i<uniterm.size();i++)
			{
				child=child+vector1[i]*vector2[i];
				dad=dad+vector1[i]*vector1[i];
				mom=mom+vector2[i]*vector2[i];
			}
			dad=Math.sqrt(dad);
			mom=Math.sqrt(mom);
			result=child/(dad*mom);
			System.out.println(news.get(j));
			System.out.println(result);
			if(result>0.3)
			{
				return 1;
			}
		}
		return 0;
	}
	
	public void runposapple() throws IOException,DocumentException,Exception   
	{
	/*	int page=5;
		int cmtcount=0;
	while(page<10)
	{
		int time=0;//用於迴圈作為計數
		Facebook facebook = new FacebookFactory().getInstance();//以下4行程式碼作為存取FQL的權限(用來取得FB留言)
		facebook.setOAuthAppId("0","0");                            
		facebook.setOAuthPermissions("0");
		facebook.setOAuthAccessToken(new AccessToken(token, null));
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		url = new URL("http://www.appledaily.com.tw/column/index/115/"+page);//藍字URL為新聞RSS
		Document xmlDoc =  Jsoup.parse(url, 3000);//使用Jsoup jar 去解析網頁	
		
		Elements title = xmlDoc.select("div.aht_title > a[href]");
		

		for (Element e :title) {
		    titles.add(e.attr("title"));
		    urls.add(e.attr("href"));
		}

		System.out.println(titles.get(2));
		System.out.println(urls.get(2).substring(25, 41));
//-----------------------------------------------------------------------------
		
		
		
		for(time=1;time<20;time++)//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
			FileWriter out = new FileWriter("allinstring.txt",false);//將留言全部合在一起成為一個string
			FileWriter outrestore=new FileWriter("afterrestore.txt",true);
			
			
			System.out.println("--------------《"+titles.get(time)+"》--------------");//新聞標題
			//outrestore.write("--------------《"+titles.get(time)+"》--------------");
			//outrestore.write("\r\n");
			JSONArray jsonArray2 =new JSONArray();//facebook4j所提供的class
			
			   jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='http://www.appledaily.com.tw/realtimenews/article/new"+urls.get(time).substring(25, 41)+"/'");
			   //爬取其他新聞的網址則沒有此問題
			
			JSONObject jsonObject2 = jsonArray2.getJSONObject(0);//用來取得該留言板的ID
			System.out.println(jsonObject2.get("comments_fbid"));
			String query = "SELECT text FROM comment WHERE object_id='"+jsonObject2.get("comments_fbid")+"'";
			JSONArray jsonArray = facebook.executeFQL(query);//將ID及抓取留言版的語法輸入至facebook FQL執行取得
			
			
			//outrestore.write("--------------《"+title.get(time+titlediff).text()+"》--------------");
			//outrestore.write("\r\n");
			//outrestore.close();
			String s;
			if(jsonArray.length()==0)
			{
				continue;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject jsonObject = jsonArray.getJSONObject(i);
			    s=jsonObject.get("text").toString().replaceAll("\\s+","");
			    s=s.toString().replaceAll("\"+","'" );
			    s=s.toString().replaceAll("、","." );
			    if(s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			    cmtcount++;
			    System.out.println(cmtcount);
			    out.write("、");
			    out.write(s);
			    
			}
			out.close();
			outrestore.close();
			FileReader fr = new FileReader("allinstring.txt");
			BufferedReader br = new BufferedReader(fr);
			 String content;
			try{
			 content=br.readLine().toString();//將此string丟入CKIP
			
			 HttpCKIP HttpCKIP = new HttpCKIP();
		     Content a=new Content(HttpCKIP.getCKIPByHTTP(content));//輸出afterrestore.txt(已還原一則一則留言(已斷詞過))
			}catch(NullPointerException e)
			{
				continue;
			}
		
		}
		page++;
	}*/
		ArrayList<Integer> posnewsid=new ArrayList<Integer>();
     	ArrayList<String> posnews=new ArrayList<String>();
		ArrayList<Double> posnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> posnewsnegrate=new ArrayList<Double>();
		
		ArrayList<Integer> negnewsid=new ArrayList<Integer>();
		ArrayList<String> negnews=new ArrayList<String>();
		ArrayList<Double> negnewsposrate=new ArrayList<Double>();
		ArrayList<Double> negnewsnegrate=new ArrayList<Double>();
		
		ArrayList<Integer> objnewsid=new ArrayList<Integer>();
		ArrayList<String> objnews=new ArrayList<String>(); 
		ArrayList<Double> objnewsposrate=new ArrayList<Double>();
		ArrayList<Double> objnewsnegrate=new ArrayList<Double>();
		
		int posindex=0,objindex=0,negindex=0;
		int getcount=0;
		int page=1;
		while(page<9)//9
		{
			int time=0;//用於迴圈作為計數
			
			
			ArrayList<String> titles = new ArrayList<String>();
			ArrayList<String> urls = new ArrayList<String>();
			URL url;//jsoup中提供的CLASS
			url = new URL("http://www.appledaily.com.tw/column/index/115/"+page);//藍字URL為新聞RSS
			Document xmlDoc =  Jsoup.parse(url, 3000);//使用Jsoup jar 去解析網頁	
			
			Elements title = xmlDoc.select("div.aht_title > a[href]");
			

			for (Element e :title) {
			    titles.add(e.attr("title"));
			    urls.add(e.attr("href"));
			  //  System.out.println(e.attr("href"));
			}
	
//-------------------------------------------------------------------------------
		ArrayList<combination> FWS=new ArrayList<combination>();
		FWS=getfws();
           
//------------------commentckip宣告----------------------		 
	     FileWriter commentckip;
//-------------------------------------------		
	    svm_predict thetrain1=new svm_predict();
	    FileReader fr3 ;//讀取predict結果
		 BufferedReader br3 ;
//------------------------------------------------
		sqlcomment anewsql;
//-------------------------清空區-----------------------↓		
	
		
		
		 int j=1;//for初始 不用再清空
		 String line2;//讀取predict結果整行(也就只有一行)
		 String[] alinearray2 = null;//將每則留言的判斷結果存入陣列
		 double pos=0.0,neg=0.0,zero=0.0;
		 double posrate=0.0,negrate=0.0,zerorate=0.0;
         String n,e,date;
         
 		sqlclass possql = new sqlclass("positive");
 		//possql.truncateTable();
 		sqlclass negsql = new sqlclass("negative");
 		//negsql.truncateTable();
 		sqlclass objsql = new sqlclass("neutral");
 		//objsql.truncateTable();
//-----------------------------------------------------------------------------
		 newlist test = new newlist();
		for(time=0;time<25;time++)//25//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
		    date=urls.get(time).substring(26,34);
		  //  System.out.println(date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8));
			String newname;
			newname=titles.get(time).toString();
			
			if(test.SelectTable(newname)!=999999)
			{
				System.out.println("新聞重複-------跳至下篇");
				continue;
			}
			System.out.println("--------------《"+titles.get(time).toString()+"》--------------");//新聞標題
			
			
			
			anewsql=new sqlcomment();  //產生留言資料庫(用以存該新聞留言)
			
			JSONArray jsonArray2 =new JSONArray();//facebook4j所提供的class
			//   jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='http://www.appledaily.com.tw/realtimenews/article/new"+urls.get(time).substring(25, 41)+"/'");
			   //爬取其他新聞的網址則沒有此問題
			
			
			
			
			String s;
			
			
			URL urlns = null;
		//Reader reader = null;
		BufferedReader brns = null;

		HttpURLConnection httpConnns = null;
		
		urlns = new URL("http://graph.facebook.com/comments?id=" 
				+ URLEncoder.encode("http://www.appledaily.com.tw/realtimenews/article/new"+urls.get(time).substring(25, 41)+"/", "UTF8"));
		httpConnns = (HttpURLConnection) urlns.openConnection();
		httpConnns.setConnectTimeout(3 * 60000);
		httpConnns.setReadTimeout(3 * 60000);
		brns = new BufferedReader(new InputStreamReader(httpConnns.getInputStream()));
		
		String strns;
		String[] alinearrayns = null;
		String[] alinearray2ns = null;
		
		
		
			
			JSONObject jsonns = new JSONObject(brns.readLine());
			strns=jsonns.toString().replace("\\n",",");
			alinearrayns=strns.split("\"message\":\"");
			alinearray2ns=strns.split("\"from\"");
			
			if(alinearrayns.length<5)
				continue;
			if(getcount>=10)
				return;
			test.insertTable("溫暖",titles.get(time).toString(), "http://www.appledaily.com.tw/realtimenews/article/new"+urls.get(time).substring(25, 41)+"/","appledaily",date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8));
			getcount++;
			for(int ii=1;ii<alinearrayns.length;ii++)
			 {
				String mixname,truename,truecomment,mixid="",trueid="";
				mixname=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('n')).substring(7);
				mixid=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('i')).substring(5);
				truename=mixname.substring(0,mixname.indexOf('"'));
				truecomment=alinearrayns[ii].substring(0,alinearrayns[ii].indexOf('"'));
				trueid="https://www.facebook.com/"+mixid.substring(0, mixid.indexOf('"'));
				 s=truecomment.replaceAll("\\s+","");
				  if(s.contains("加賴")||s.contains("+賴")||s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			    	
			    n =truename;
			    e =truecomment;
			    golcomment=e;
			    
			    commentckip=new FileWriter("commentckip.txt",false);
			    CKIP c = new CKIP("140.109.19.104" , 1501, "text48953", "ieliao");
				
				c.setRawText(golcomment.replaceAll("\\s+",""));
				c.send();		
	
				for(Term t : c.getTerm()){
					// System.out.print(t.getTerm()+", ");
					 commentckip.write(t.getTerm()+", ");
			                              }
				//System.out.println("\n");
				commentckip.write("\r\n");
				 commentckip.close();
//------------------------------------------------------------------				 
				getcommentformat(FWS);
//-------------------------------------------------------------------		
				     String[] p={"format.txt","0406trainingdata.txt.model","result.txt"};
				    thetrain1.main(p);
				    fr3 = new FileReader("result.txt");
				    br3 = new BufferedReader(fr3);
				    int codenum=1;
				    
				    while (br3.ready())
					  {
						 line2=br3.readLine().toString();
						 alinearray2=line2.split(" ");
						 for(int i=0;i<alinearray2.length;i++)
						 {
							if(alinearray2[i].compareTo("1.0")==0)
							{
								pos++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"P",trueid);
								codenum++;
								
							}
						
							else if(alinearray2[i].compareTo("-1.0")==0)
							{
								neg++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"N",trueid);
								codenum++;
							}
							
								
						 }      
					  }
				    fr3.close();
				
			   
			 }//全部留言
			
			
		
				
			        if(pos+neg+zero==0)
			    	{
			        	 line2="";
						// Arrays.fill(alinearray2, (String) "" );
						  pos=0.0;
						  neg=0.0;
						  zero=0.0;
			    	  continue;
			    	
			    	}
			    
			    posrate=pos*100.0/(pos+neg+zero);
			    negrate=neg*100.0/(pos+neg+zero);
			    zerorate=zero*100.0/(pos+neg+zero);
			    DecimalFormat df = new DecimalFormat("##.00");
			    posrate= Double.parseDouble(df.format(posrate));
			    negrate=Double.parseDouble(df.format(negrate));
			    zerorate=Double.parseDouble(df.format(zerorate));
				 System.out.println("正面:"+pos+"中立:"+zero+"負面:"+neg);
				 
				 if(zerorate>=50)
				 {
					 objsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.appledaily.com.tw/realtimenews/article/new"+urls.get(time).substring(25, 41)+"/");
					
				 }
				 else if(posrate>negrate&&(posrate-negrate)>10)
				 {
					 possql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.appledaily.com.tw/realtimenews/article/new"+urls.get(time).substring(25, 41)+"/");
				
				 }
				 else if(negrate>posrate&&(negrate-posrate)>10)
				 {
					 negsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.appledaily.com.tw/realtimenews/article/new"+urls.get(time).substring(25, 41)+"/");
	
				 }
				 else
				 {
					 objsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.appledaily.com.tw/realtimenews/article/new"+urls.get(time).substring(25, 41)+"/");
					
				 }

					
				 line2="";
				 Arrays.fill(alinearray2, (String) "" );
				  pos=0.0;
				  neg=0.0;
				  zero=0.0;
					 
					
					
		}

		page++;
		posnewsid.clear();
		posnews.clear();
		posnewsposrate.clear();
		posnewsnegrate.clear();
		
		negnewsid.clear();
		negnews.clear();
		negnewsposrate.clear();
		negnewsnegrate.clear();
		
		objnewsid.clear();
		objnews.clear();
		objnewsposrate.clear();
		objnewsnegrate.clear();
	  }
		
	}
	public void runltnhot() throws IOException,DocumentException,Exception  
	{
		/*int page=1;
		while(page<6)
		{	
		int time=0;//用於迴圈作為計數
		Facebook facebook = new FacebookFactory().getInstance();//以下4行程式碼作為存取FQL的權限(用來取得FB留言)
		facebook.setOAuthAppId("0","0");                            
		facebook.setOAuthPermissions("0");
		facebook.setOAuthAccessToken(new AccessToken("CAACEdEose0cBANMxiip6eT2HEY8BXisjcyAYpXdNWtrGvVoVTKMBIGi2cfzbeQdF18JZCkOZBj0kq9KQbVhMeeZBsNskakB5MvcaUQRJukfpasQ50jsXMPr4TiJo67aphwBr9lLGoZCZBAEuL7Aw6BFN6ByKUuJA4BFA13GAB2QOPoZBxuSI4aeZCRGNqM6qmznbVIvUw53yAZDZD", null));
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		url = new URL("http://news.ltn.com.tw/list/Highlights?page="+page);//藍字URL為新聞RSS
		Document xmlDoc =  Jsoup.parse(url, 3000);//使用Jsoup jar 去解析網頁	
		
		Elements title = xmlDoc.select("li.litab").select("a");
		
        
		for (Element e :title) {
			if(e.text().equals(""))
				continue;
		    titles.add(e.text());
		    urls.add(e.attr("href"));
		  //  System.out.println(e.attr("href"));
		   // break;
		   
		}

		//System.out.println(titles.get(1));
		//System.out.println(urls.get(0));
//-----------------------------------------------------------------------------
		
		
		
		for(time=0;time<10;time++)//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
			FileWriter out = new FileWriter("allinstring.txt",false);//將留言全部合在一起成為一個string
			FileWriter outrestore=new FileWriter("afterrestore.txt",true);
			
			
			System.out.println("--------------《"+titles.get(time)+"》--------------");//新聞標題
			//outrestore.write("--------------《"+titles.get(time)+"》--------------");
			//outrestore.write("\r\n");
			JSONArray jsonArray2 =new JSONArray();//facebook4j所提供的class
			
			   jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='http://news.ltn.com.tw"+urls.get(time)+"'");
			   //爬取其他新聞的網址則沒有此問題
			
			JSONObject jsonObject2 = jsonArray2.getJSONObject(0);//用來取得該留言板的ID
			System.out.println(jsonObject2.get("comments_fbid"));
			String query = "SELECT text FROM comment WHERE object_id='"+jsonObject2.get("comments_fbid")+"'";
			JSONArray jsonArray = facebook.executeFQL(query);//將ID及抓取留言版的語法輸入至facebook FQL執行取得
			
			
			//outrestore.write("--------------《"+title.get(time+titlediff).text()+"》--------------");
			//outrestore.write("\r\n");
			//outrestore.close();
			String s;
			if(jsonArray.length()<10)
			{
				continue;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject jsonObject = jsonArray.getJSONObject(i);
			    s=jsonObject.get("text").toString().replaceAll("\\s+","");
			    s=s.toString().replaceAll("\"+","'" );
			    s=s.toString().replaceAll("、","." );
			    if(s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			   // System.out.println(s);
			    out.write("、");
			    out.write(s);
			    
			}
			out.close();
			outrestore.close();
			FileReader fr = new FileReader("allinstring.txt");
			BufferedReader br = new BufferedReader(fr);
			 String content;
			try{
			 content=br.readLine().toString();//將此string丟入CKIP
			
			 HttpCKIP HttpCKIP = new HttpCKIP();
		     Content a=new Content(HttpCKIP.getCKIPByHTTP(content));//輸出afterrestore.txt(已還原一則一則留言(已斷詞過))
			}catch(NullPointerException e)
			{
				continue;
			}
		
		}
		page++;
		}*/
		ArrayList<Integer> posnewsid=new ArrayList<Integer>();
     	ArrayList<String> posnews=new ArrayList<String>();
     	ArrayList<Double> posnewsposrate=new ArrayList<Double>(); 
     	ArrayList<Double> posnewsnegrate=new ArrayList<Double>();
		
		ArrayList<Integer> negnewsid=new ArrayList<Integer>();
		ArrayList<String> negnews=new ArrayList<String>();
		ArrayList<Double> negnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> negnewsnegrate=new ArrayList<Double>(); 
		
		ArrayList<Integer> objnewsid=new ArrayList<Integer>();
		ArrayList<String> objnews=new ArrayList<String>(); 
		ArrayList<Double> objnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> objnewsnegrate=new ArrayList<Double>();
		
		int posindex=0,objindex=0,negindex=0;
		int getcount=0;
		int page=1;
		while(page<6)//6
		{	
		int time=0;//用於迴圈作為計數
		
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		url = new URL("http://news.ltn.com.tw/list/Highlights?page="+page);//藍字URL為新聞RSS
		Document xmlDoc =  Jsoup.parse(url, 3000);//使用Jsoup jar 去解析網頁	
		
		Elements title = xmlDoc.select("li.litab").select("a");
		Elements date  = xmlDoc.select("li.litab").select("span");
        
		for (Element e :title) {
			if(e.text().equals(""))
				continue;
		    titles.add(e.text());
		    urls.add(e.attr("href"));
		  //  System.out.println(e.attr("href"));
		 //   System.out.println(e.text());
		   // break;
		   
		}
		for (Element q :date) {
			if(q.text().equals(""))
				continue;
		    dates.add(q.text());
		    //System.out.println(q.text());
		   // break;
		   
		}
	
//-------------------------------------------------------------------------------
		ArrayList<combination> FWS=new ArrayList<combination>();
		FWS=getfws();
           
//------------------commentckip宣告----------------------		 
	     FileWriter commentckip;
//-------------------------------------------		
	    svm_predict thetrain1=new svm_predict();
	    FileReader fr3 ;//讀取predict結果
		 BufferedReader br3 ;
//------------------------------------------------
		sqlcomment anewsql;
//-------------------------清空區-----------------------↓		
	
		
		
		 int j=1;//for初始 不用再清空
		 String line2;//讀取predict結果整行(也就只有一行)
		 String[] alinearray2 = null;//將每則留言的判斷結果存入陣列
		 double pos=0.0,neg=0.0,zero=0.0;
		 double posrate=0.0,negrate=0.0,zerorate=0.0;
         String n,e;
 		sqlclass possql = new sqlclass("positive");
 		//possql.truncateTable();
 		sqlclass negsql = new sqlclass("negative");
 		//negsql.truncateTable();
 		sqlclass objsql = new sqlclass("neutral");
//-----------------------------------------------------------------------------
		 newlist test = new newlist();
		for(time=0;time<20;time++)//20//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
		
			String newname;
            newname=titles.get(time).toString();
			
			if(test.SelectTable(newname)!=999999)
			{
				System.out.println("新聞重複-------跳至下篇");
				continue;
			}
			System.out.println("--------------《"+titles.get(time).toString()+"》--------------");//新聞標題
			
			
			anewsql=new sqlcomment();  //產生留言資料庫(用以存該新聞留言)
			
		
			
	
			
			
			
			   
			   //爬取其他新聞的網址則沒有此問題
			
			
			
			
			String s;
			String fy;
		
			if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("BreakingNews"))
				fy="即時";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("newspaper"))
				fy="報紙";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("focus"))
				fy="焦點";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("politics"))
				fy="政治";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("society"))
				fy="社會";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("local"))
				fy="地方";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("life"))
				fy="生活";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("talk"))
				fy="言論";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("world"))
				fy="國際";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("business"))
				fy="財經";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("sports"))
				fy="體育";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("entertainment"))
				fy="娛樂";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("consumer"))
				fy="消費";
			else if(urls.get(time).substring(6,6+urls.get(time).substring(6).indexOf('/') ).equals("supplement"))
				fy="副刊";
			else
				fy="unknown";
			
			
			URL urlns = null;
		//Reader reader = null;
		BufferedReader brns = null;

		HttpURLConnection httpConnns = null;
		
		urlns = new URL("http://graph.facebook.com/comments?id=" 
				+ URLEncoder.encode("http://news.ltn.com.tw"+urls.get(time), "UTF8"));
		httpConnns = (HttpURLConnection) urlns.openConnection();
		httpConnns.setConnectTimeout(3 * 60000);
		httpConnns.setReadTimeout(3 * 60000);
		try{
		brns = new BufferedReader(new InputStreamReader(httpConnns.getInputStream()));
		}catch(IOException eee)
		{
			continue;
		}
		
		String strns;
		String[] alinearrayns = null;
		String[] alinearray2ns = null;
		
		
		
			
			JSONObject jsonns = new JSONObject(brns.readLine());
			strns=jsonns.toString().replace("\\n",",");
			alinearrayns=strns.split("\"message\":\"");
			alinearray2ns=strns.split("\"from\"");
			
			if(alinearrayns.length<10)
				continue;
			if(getcount>=10)
				return;
			getcount++;
			test.insertTable(fy,titles.get(time).toString(), "http://news.ltn.com.tw"+urls.get(time),"ltn",dates.get(time).substring(0,10));
			for(int ii=1;ii<alinearrayns.length;ii++)
			 {
				String mixname,truename,truecomment,mixid="",trueid="";
				mixname=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('n')).substring(7);
				mixid=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('i')).substring(5);
				truename=mixname.substring(0,mixname.indexOf('"'));
				truecomment=alinearrayns[ii].substring(0,alinearrayns[ii].indexOf('"'));
				trueid="https://www.facebook.com/"+mixid.substring(0, mixid.indexOf('"'));
				 s=truecomment.replaceAll("\\s+","");
				  if(s.contains("加賴")||s.contains("+賴")||s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			    	
			    n =truename;
			    e =truecomment;
			    golcomment=e;
			    
			    commentckip=new FileWriter("commentckip.txt",false);
			    CKIP c = new CKIP("140.109.19.104" , 1501, "text48953", "ieliao");
				
				c.setRawText(golcomment.replaceAll("\\s+",""));
				c.send();		
	
				for(Term t : c.getTerm()){
					// System.out.print(t.getTerm()+", ");
					 commentckip.write(t.getTerm()+", ");
			                              }
				//System.out.println("\n");
				commentckip.write("\r\n");
				 commentckip.close();
//------------------------------------------------------------------				 
				getcommentformat(FWS);
//-------------------------------------------------------------------		
				     String[] p={"format.txt","0406trainingdata.txt.model","result.txt"};
				    thetrain1.main(p);
				    fr3 = new FileReader("result.txt");
				    br3 = new BufferedReader(fr3);
				    int codenum=1;
				    while (br3.ready())
					  {
						 line2=br3.readLine().toString();
						 alinearray2=line2.split(" ");
						 for(int i=0;i<alinearray2.length;i++)
						 {
							if(alinearray2[i].compareTo("1.0")==0)
							{
								pos++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"P",trueid);
								codenum++;
								
							}
						
							else if(alinearray2[i].compareTo("-1.0")==0)
							{
								neg++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"N",trueid);
								codenum++;
							}
							
								
						 }      
					  }
				    fr3.close();
				
			   
			 }//全部留言
			        if(pos+neg+zero==0)
			    	{
			        	 line2="";
					//	 Arrays.fill(alinearray2, (String) "" );
						  pos=0;
						  neg=0;
						  zero=0;
			    	  continue;
			    	
			    	}
			    posrate=pos*100.0/(pos+neg+zero);
			    negrate=neg*100.0/(pos+neg+zero);
			    zerorate=zero*100.0/(pos+neg+zero);
			    DecimalFormat df = new DecimalFormat("##.00");
			    posrate= Double.parseDouble(df.format(posrate));
			    negrate=Double.parseDouble(df.format(negrate));
			    zerorate=Double.parseDouble(df.format(zerorate));
				 System.out.println("正面:"+pos+"中立:"+zero+"負面:"+neg);
				 
				 if(zerorate>=50)
				 {
					 objsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://news.ltn.com.tw"+urls.get(time));
					
					 
					 
				 }
				 else if(posrate>negrate&&(posrate-negrate)>10)
				 {
					 possql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://news.ltn.com.tw"+urls.get(time));
					
					
				 }
				 else if(negrate>posrate&&(negrate-posrate)>10)
				 {
					 negsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://news.ltn.com.tw"+urls.get(time));
				
					
				 }
				 else
				 {
					 objsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://news.ltn.com.tw"+urls.get(time));
					
					
				 }

					
				 line2="";
				 Arrays.fill(alinearray2, (String) "" );
				  pos=0;
				  neg=0;
				  zero=0;
					 
					
					
		}

		page++;
		posnewsid.clear();
		posnews.clear();
		posnewsposrate.clear();
		posnewsnegrate.clear();
		
		negnewsid.clear();
		negnews.clear();
		negnewsposrate.clear();
		negnewsnegrate.clear();
		
		objnewsid.clear();
		objnews.clear();
		objnewsposrate.clear();
		objnewsnegrate.clear();
	  }
	}
	public void runettodayhot() throws IOException,DocumentException,Exception//name過長
	{
		
		/*int time=0;//用於迴圈作為計數
		Facebook facebook = new FacebookFactory().getInstance();//以下4行程式碼作為存取FQL的權限(用來取得FB留言)
		facebook.setOAuthAppId("0","0");                            
		facebook.setOAuthPermissions("0");
		facebook.setOAuthAccessToken(new AccessToken("CAACEdEose0cBANYHqy0t7iKOO3VcJJltvzicIgljRVCxCdNoRtRl9IKVlfZAotEHdd3duvKB2qnIQQVw3vgBQyb82RUO3tpbZA2wRhAluLyZAZBF7nfYGXBjuWahKZA8jnxLUxHQo5dA4DKpVub4SsqPuetSq4AonNzstMpNVYha73Jx3kA0g74MObjiJmsoZCkpQ3BFcLZCgZDZD", null));
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		
		Document xmlDoc =  Jsoup.connect("http://www.ettoday.net/news/hot-news.htm")
		           .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
		           .get();
		//Elements title = xmlDoc.select("div.box_0.clearfix").select("h3").select("a");
		Elements title = xmlDoc.select("div.box_0.clearfix");
		Elements date = xmlDoc.select("div.box_0.clearfix").select("p");
        int cc=0;
        int dd=0;
		for (Element e :title) {
			if(!(e.select("h3").select("a").attr("href").substring(0,5).equals("/news")))
				continue;
		    titles.add(e.select("h3").select("a").text());
		    urls.add(e.select("h3").select("a").attr("href"));
		    dates.add(e.select("span").text().substring(1,11).replace('-','	').replaceAll("\\s+",""));
		    System.out.println(e.select("span").text().substring(1,11).replace('-','	').replaceAll("\\s+",""));
		    System.out.println(e.select("h3").select("a").attr("href"));
		    System.out.println(++cc);
		    
		   // break;
		   
		}
	

		//System.out.println(titles.get(1));
		//System.out.println(urls.get(0));
//-----------------------------------------------------------------------------
		
		
		
		for(time=0;time<50;time++)//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
			FileWriter out = new FileWriter("allinstring.txt",false);//將留言全部合在一起成為一個string
			FileWriter outrestore=new FileWriter("afterrestore.txt",true);
			
			
			System.out.println("--------------《"+titles.get(time)+"》--------------");//新聞標題
			//outrestore.write("--------------《"+titles.get(time)+"》--------------");
			//outrestore.write("\r\n");
			JSONArray jsonArray2 =new JSONArray();//facebook4j所提供的class
			
			   jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='http://www.ettoday.net"+urls.get(time).substring(0, 6)+dates.get(time)+urls.get(time).substring(14, 25)+"'");
			   //爬取其他新聞的網址則沒有此問題
			
			JSONObject jsonObject2 = jsonArray2.getJSONObject(0);//用來取得該留言板的ID
			System.out.println(jsonObject2.get("comments_fbid"));
			String query = "SELECT text FROM comment WHERE object_id='"+jsonObject2.get("comments_fbid")+"'";
			JSONArray jsonArray = facebook.executeFQL(query);//將ID及抓取留言版的語法輸入至facebook FQL執行取得
			
			
			//outrestore.write("--------------《"+title.get(time+titlediff).text()+"》--------------");
			//outrestore.write("\r\n");
			//outrestore.close();
			String s;
			if(jsonArray.length()==0)
			{
				continue;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject jsonObject = jsonArray.getJSONObject(i);
			    s=jsonObject.get("text").toString().replaceAll("\\s+","");
			    s=s.toString().replaceAll("\"+","'" );
			    s=s.toString().replaceAll("、","." );
			    if(s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			   // System.out.println(s);
			    out.write("、");
			    out.write(s);
			    
			}
			out.close();
			outrestore.close();
			FileReader fr = new FileReader("allinstring.txt");
			BufferedReader br = new BufferedReader(fr);
			 String content;
			try{
			 content=br.readLine().toString();//將此string丟入CKIP
			
			 HttpCKIP HttpCKIP = new HttpCKIP();
		     Content a=new Content(HttpCKIP.getCKIPByHTTP(content));//輸出afterrestore.txt(已還原一則一則留言(已斷詞過))
			}catch(NullPointerException e)
			{
				continue;
			}
		
		}*/
		ArrayList<Integer> posnewsid=new ArrayList<Integer>();
     	ArrayList<String> posnews=new ArrayList<String>();
     	ArrayList<Double> posnewsposrate=new ArrayList<Double>(); 
     	ArrayList<Double> posnewsnegrate=new ArrayList<Double>(); 
		
		ArrayList<Integer> negnewsid=new ArrayList<Integer>();
		ArrayList<String> negnews=new ArrayList<String>();
		ArrayList<Double> negnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> negnewsnegrate=new ArrayList<Double>();
		
		ArrayList<Integer> objnewsid=new ArrayList<Integer>();
		ArrayList<String> objnews=new ArrayList<String>(); 
		ArrayList<Double> objnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> objnewsnegrate=new ArrayList<Double>();
		
		int posindex=0,objindex=0,negindex=0;
		int getcount=0;
		int time=0;//用於迴圈作為計數
	
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		
		Document xmlDoc =  Jsoup.connect("http://www.ettoday.net/news/hot-news.htm")
		           .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
		           .get();
		//Elements title = xmlDoc.select("div.box_0.clearfix").select("h3").select("a");
		Elements title = xmlDoc.select("div.box_0.clearfix");
		Elements date = xmlDoc.select("div.box_0.clearfix").select("p");
        int cc=0;
        int dd=0;
		for (Element e :title) {
			if(!(e.select("h3").select("a").attr("href").substring(0,5).equals("/news")))
				continue;
		    titles.add(e.select("h3").select("a").text());
		    urls.add(e.select("h3").select("a").attr("href"));
		    dates.add(e.select("span").text().substring(1,11).replace('-','	').replaceAll("\\s+",""));
		 //   System.out.println(e.select("span").text().substring(1,11).replace('-','	').replaceAll("\\s+",""));
		   // System.out.println(e.select("h3").select("a").attr("href"));
		    //System.out.println(++cc);
		    
		   // break;
		   
		}
	
//-------------------------------------------------------------------------------
		ArrayList<combination> FWS=new ArrayList<combination>();
		FWS=getfws();
           
//------------------commentckip宣告----------------------		 
	     FileWriter commentckip;
//-------------------------------------------		
	    svm_predict thetrain1=new svm_predict();
	    FileReader fr3 ;//讀取predict結果
		 BufferedReader br3 ;
//------------------------------------------------
		sqlcomment anewsql;
//-------------------------清空區-----------------------↓		
	
		
		
		 int j=1;//for初始 不用再清空
		 String line2;//讀取predict結果整行(也就只有一行)
		 String[] alinearray2 = null;//將每則留言的判斷結果存入陣列
		 double pos=0.0,neg=0.0,zero=0.0;
		 double posrate=0.0,negrate=0.0,zerorate=0.0;
         String n,e,categoryET;
         
 		sqlclass possql = new sqlclass("positive");
 		//possql.truncateTable();
 		sqlclass negsql = new sqlclass("negative");
 		//negsql.truncateTable();
 		sqlclass objsql = new sqlclass("neutral");
 		//objsql.truncateTable();
//-----------------------------------------------------------------------------
		 newlist test = new newlist();
		for(time=0;time<45;time++)//45
		{	
		
			String newname;
            newname=titles.get(time).toString();
			
			if(test.SelectTable(newname)!=999999)
			{
				System.out.println("新聞重複-------跳至下篇");
				continue;
			}
			Document xmlDoc3 =  Jsoup.connect("http://www.ettoday.net"+urls.get(time).substring(0, 6)+dates.get(time)+urls.get(time).substring(14, 25))
			           .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
			           .get();
			//Elements title = xmlDoc.select("div.box_0.clearfix").select("h3").select("a");
			Element category = xmlDoc3.select("meta[name=section]").first();
			try
			{
				categoryET=category.attr("content").toString();
			}
			catch(NullPointerException yy)
			{
				categoryET="綜合";
			}
			System.out.println("--------------《"+titles.get(time).toString()+"》--------------"+dates.get(time).substring(0,4)+"-"+dates.get(time).substring(4,6)+"-"+dates.get(time).substring(6,8));//新聞標題
			
			
			anewsql=new sqlcomment();  //產生留言資料庫(用以存該新聞留言)
	
			String s;
			
			
			URL urlns = null;
		//Reader reader = null;
		BufferedReader brns = null;

		HttpURLConnection httpConnns = null;
		
		urlns = new URL("http://graph.facebook.com/comments?id=" 
				+ URLEncoder.encode("http://www.ettoday.net"+urls.get(time).substring(0, 6)+dates.get(time)+urls.get(time).substring(14, 25), "UTF8"));
		httpConnns = (HttpURLConnection) urlns.openConnection();
		httpConnns.setConnectTimeout(3 * 60000);
		httpConnns.setReadTimeout(3 * 60000);
		brns = new BufferedReader(new InputStreamReader(httpConnns.getInputStream()));
		
		String strns;
		String[] alinearrayns = null;
		String[] alinearray2ns = null;
		
		
		
			
			JSONObject jsonns = new JSONObject(brns.readLine());
			strns=jsonns.toString().replace("\\n",",");
			alinearrayns=strns.split("\"message\":\"");
			alinearray2ns=strns.split("\"from\"");
			
			if(alinearrayns.length<1)
				continue;
			if(getcount>=10)
				return;
			getcount++;
			
			test.insertTable(categoryET,titles.get(time).toString(), "http://www.ettoday.net"+urls.get(time).substring(0, 6)+dates.get(time)+urls.get(time).substring(14, 25),"ettoday",dates.get(time).substring(0,4)+"-"+dates.get(time).substring(4,6)+"-"+dates.get(time).substring(6,8));
			for(int ii=1;ii<alinearrayns.length;ii++)
			 {
				String mixname,truename,truecomment,mixid="",trueid="";
				mixname=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('n')).substring(7);
				mixid=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('i')).substring(5);
				truename=mixname.substring(0,mixname.indexOf('"'));
				truecomment=alinearrayns[ii].substring(0,alinearrayns[ii].indexOf('"'));
				trueid="https://www.facebook.com/"+mixid.substring(0, mixid.indexOf('"'));
				 s=truecomment.replaceAll("\\s+","");
				  if(s.contains("加賴")||s.contains("+賴")||s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			    	
			    n =truename;
			    e =truecomment;
			    golcomment=e;
			    
			    commentckip=new FileWriter("commentckip.txt",false);
			    CKIP c = new CKIP("140.109.19.104" , 1501, "text48953", "ieliao");
				
				c.setRawText(golcomment.replaceAll("\\s+",""));
				c.send();		
	
				for(Term t : c.getTerm()){
					// System.out.print(t.getTerm()+", ");
					 commentckip.write(t.getTerm()+", ");
			                              }
				//System.out.println("\n");
				commentckip.write("\r\n");
				 commentckip.close();
//------------------------------------------------------------------				 
				getcommentformat(FWS);
//-------------------------------------------------------------------		
				     String[] p={"format.txt","0406trainingdata.txt.model","result.txt"};
				    thetrain1.main(p);
				    fr3 = new FileReader("result.txt");
				    br3 = new BufferedReader(fr3);
				    int codenum=1;
				    while (br3.ready())
					  {
						 line2=br3.readLine().toString();
						 alinearray2=line2.split(" ");
						 for(int i=0;i<alinearray2.length;i++)
						 {
							if(alinearray2[i].compareTo("1.0")==0)
							{
								pos++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"P",trueid);
								codenum++;
								
							}
						
							else if(alinearray2[i].compareTo("-1.0")==0)
							{
								neg++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"N",trueid);
								codenum++;
							}
							
								
						 }      
					  }
				    fr3.close();
				
			   
			 }//全部留言
			        if(pos+neg+zero==0)
			    	{
			        	 line2="";
						// Arrays.fill(alinearray2, (String) "" );
						  pos=0;
						  neg=0;
						  zero=0;
			    	  continue;
			    	
			    	}
			    posrate=pos*100.0/(pos+neg+zero);
			    negrate=neg*100.0/(pos+neg+zero);
			    zerorate=zero*100.0/(pos+neg+zero);
			    DecimalFormat df = new DecimalFormat("##.00");
			    posrate= Double.parseDouble(df.format(posrate));
			    negrate=Double.parseDouble(df.format(negrate));
			    zerorate=Double.parseDouble(df.format(zerorate));
				 System.out.println("正面:"+pos+"中立:"+zero+"負面:"+neg);
				 
				 if(zerorate>=50)
				 {
					 objsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.ettoday.net"+urls.get(time).substring(0, 6)+dates.get(time)+urls.get(time).substring(14, 25));
						
				 }
				 else if(posrate>negrate&&(posrate-negrate)>10)
				 {
					 possql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.ettoday.net"+urls.get(time).substring(0, 6)+dates.get(time)+urls.get(time).substring(14, 25));
				
					 
				 }
				 else if(negrate>posrate&&(negrate-posrate)>10)
				 {
					 negsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.ettoday.net"+urls.get(time).substring(0, 6)+dates.get(time)+urls.get(time).substring(14, 25));
				
					 
				 }
				 else
				 {
					 objsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.ettoday.net"+urls.get(time).substring(0, 6)+dates.get(time)+urls.get(time).substring(14, 25));	
					 
				 }

					
				 line2="";
				 Arrays.fill(alinearray2, (String) "" );
				  pos=0;
				  neg=0;
				  zero=0;
					 
					
					
		}

		
		
	}
	public void runsetnhot() throws IOException,DocumentException,Exception//有10頁待爬  
	{
		
		/*int time=0;//用於迴圈作為計數
		Facebook facebook = new FacebookFactory().getInstance();//以下4行程式碼作為存取FQL的權限(用來取得FB留言)
		facebook.setOAuthAppId("0","0");                            
		facebook.setOAuthPermissions("0");
		facebook.setOAuthAccessToken(new AccessToken("CAACEdEose0cBAI0gi1B865nKrKVlYuoPiNMgQL7xuEX0bgNT2spJQiG20kHvzrQo1tDPZBnMNK9ipZAmw81r7UNTNon6jeDGztOAgBaDY3ZCdxCLapMHVRYnPHfvT5EmspbJHqdLvBSmKQmRp0r7Hs6CYcEtZAW3cFxLAecZBPQUrDYTM8KzI7sJ2yTYiZBQrgdss9bBoF4LZAoXJZCrmpzZA", null));
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		
		Document xmlDoc =  Jsoup.connect("http://www.setn.com/ViewAll.aspx?pagegroupid=0&p=1")
		           .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
		           .get();
		Elements title = xmlDoc.select("div.box").select("a");
		
        
		for (Element e :title) {
			if(!(e.attr("href").substring(0,5).equals("/News")))
				continue;
		    titles.add(e.text());
		    urls.add(e.attr("href"));
		    System.out.println(e.attr("href"));
		   // break;
		   
		}

		//System.out.println(titles.get(1));
		//System.out.println(urls.get(0));
//-----------------------------------------------------------------------------
		
		
		
		for(time=0;time<30;time++)//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
			FileWriter out = new FileWriter("allinstring.txt",false);//將留言全部合在一起成為一個string
			FileWriter outrestore=new FileWriter("afterrestore.txt",true);
			
			
			System.out.println("--------------《"+titles.get(time)+"》--------------");//新聞標題
			//outrestore.write("--------------《"+titles.get(time)+"》--------------");
			//outrestore.write("\r\n");
			JSONArray jsonArray2 =new JSONArray();//facebook4j所提供的class
			
			   jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='http://www.setn.com"+urls.get(time).substring(0,24)+"'");
			   //爬取其他新聞的網址則沒有此問題
			
			JSONObject jsonObject2 = jsonArray2.getJSONObject(0);//用來取得該留言板的ID
			System.out.println(jsonObject2.get("comments_fbid"));
			
			if(jsonObject2.get("comments_fbid").toString().equals("null"))
			{
				jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='http://www.setn.com/E"+urls.get(time).substring(0,24)+"'");
				jsonObject2 = jsonArray2.getJSONObject(0);//用來取得該留言板的ID
				
			}
			String query = "SELECT text FROM comment WHERE object_id='"+jsonObject2.get("comments_fbid")+"'";
			JSONArray jsonArray = facebook.executeFQL(query);//將ID及抓取留言版的語法輸入至facebook FQL執行取得
			
			
			//outrestore.write("--------------《"+title.get(time+titlediff).text()+"》--------------");
			//outrestore.write("\r\n");
			//outrestore.close();
			String s;
			if(jsonArray.length()==0)
			{
				continue;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject jsonObject = jsonArray.getJSONObject(i);
			    s=jsonObject.get("text").toString().replaceAll("\\s+","");
			    s=s.toString().replaceAll("\"+","'" );
			    s=s.toString().replaceAll("、","." );
			    if(s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			   // System.out.println(s);
			    out.write("、");
			    out.write(s);
			    
			}
			out.close();
			outrestore.close();
			FileReader fr = new FileReader("allinstring.txt");
			BufferedReader br = new BufferedReader(fr);
			 String content;
			try{
			 content=br.readLine().toString();//將此string丟入CKIP
			
			 HttpCKIP HttpCKIP = new HttpCKIP();
		     Content a=new Content(HttpCKIP.getCKIPByHTTP(content));//輸出afterrestore.txt(已還原一則一則留言(已斷詞過))
			}catch(NullPointerException e)
			{
				continue;
			}
		
		}*/
		ArrayList<Integer> posnewsid=new ArrayList<Integer>();
     	ArrayList<String> posnews=new ArrayList<String>();
     	ArrayList<Double> posnewsposrate=new ArrayList<Double>(); 
     	ArrayList<Double> posnewsnegrate=new ArrayList<Double>(); 
		
		ArrayList<Integer> negnewsid=new ArrayList<Integer>();
		ArrayList<String> negnews=new ArrayList<String>();
		ArrayList<Double> negnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> negnewsnegrate=new ArrayList<Double>();
		
		ArrayList<Integer> objnewsid=new ArrayList<Integer>();
		ArrayList<String> objnews=new ArrayList<String>(); 
		ArrayList<Double> objnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> objnewsnegrate=new ArrayList<Double>();
		
		int posindex=0,objindex=0,negindex=0;
		int getcount=0;
		int page=1;
		while(page<6)//6
	{
		int time=0;//用於迴圈作為計數
	
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> categories = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		
		Document xmlDoc =  Jsoup.connect("http://www.setn.com/ViewAll.aspx?pagegroupid=0&p="+page)
		           .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
		           .get();
		Elements title = xmlDoc.select("div.box").select("li");
		Elements date = xmlDoc.select("div.box");
        
		for (Element e :title) {
			if(!(e.select("a").attr("href").substring(0,5).equals("/News")))
				continue;
		    titles.add(e.select("a").text());
		    urls.add(e.select("a").attr("href"));
		    dates.add(e.select("span.tab_list_time").text());
		    categories.add(e.select("span.tab_list_type").text());
		   // System.out.println(e.select("a").attr("href"));
		   // System.out.println(e.select("a").text());
		   // System.out.println(e.select("span.tab_list_time").text());
		   // break;
		   
		}

	
//-------------------------------------------------------------------------------
		ArrayList<combination> FWS=new ArrayList<combination>();
		FWS=getfws();
           
//------------------commentckip宣告----------------------		 
	     FileWriter commentckip;
//-------------------------------------------		
	    svm_predict thetrain1=new svm_predict();
	    FileReader fr3 ;//讀取predict結果
		 BufferedReader br3 ;
//------------------------------------------------
		sqlcomment anewsql;
//-------------------------清空區-----------------------↓		
	
		
		
		 int j=1;//for初始 不用再清空
		 String line2;//讀取predict結果整行(也就只有一行)
		 String[] alinearray2 = null;//將每則留言的判斷結果存入陣列
		 double pos=0.0,neg=0.0,zero=0.0;
		 double posrate=0.0,negrate=0.0,zerorate=0.0;
         String n,e;
         
 		sqlclass possql = new sqlclass("positive");
 		//possql.truncateTable();
 		sqlclass negsql = new sqlclass("negative");
 		//negsql.truncateTable();
 		sqlclass objsql = new sqlclass("neutral");
 		//objsql.truncateTable();
//-----------------------------------------------------------------------------
		 newlist test = new newlist();
		for(time=0;time<25;time++)//25//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
		
			String newname;
            newname=titles.get(time).toString();
			
			if(test.SelectTable(newname)!=999999)
			{
				System.out.println("新聞重複-------跳至下篇");
				continue;
			}
			System.out.println("--------------《"+titles.get(time).toString()+"》--------------");//新聞標題
			
			
			anewsql=new sqlcomment();  //產生留言資料庫(用以存該新聞留言)
			
			
			
	
			
			
			
			 
			
			String s;
	
			
			
			URL urlns = null;
		//Reader reader = null;
		BufferedReader brns = null;

		HttpURLConnection httpConnns = null;
		
		urlns = new URL("http://graph.facebook.com/comments?id=" 
				+ URLEncoder.encode("http://www.setn.com"+urls.get(time).substring(0,24), "UTF8"));
		httpConnns = (HttpURLConnection) urlns.openConnection();
		httpConnns.setConnectTimeout(3 * 60000);
		httpConnns.setReadTimeout(3 * 60000);
		try{
		brns = new BufferedReader(new InputStreamReader(httpConnns.getInputStream()));
		}catch(IOException ee)
		{
			continue;
		}
		
		
		String strns;
		String[] alinearrayns = null;
		String[] alinearray2ns = null;
		
		
		
			
			JSONObject jsonns = new JSONObject(brns.readLine());
			strns=jsonns.toString().replace("\\n",",");
			alinearrayns=strns.split("\"message\":\"");
			alinearray2ns=strns.split("\"from\"");
			
			if(alinearrayns.length<10)
				continue;
			if(getcount>=10)
				return;
			getcount++;
			test.insertTable(categories.get(time).toString(),titles.get(time).toString(),"http://www.setn.com"+urls.get(time).toString(),"setn","2016-"+dates.get(time).substring(0,2)+"-"+dates.get(time).substring(3,5));
			for(int ii=1;ii<alinearrayns.length;ii++)
			 {
				String mixname,truename,truecomment,mixid="",trueid="";
				mixname=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('n')).substring(7);
				mixid=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('i')).substring(5);
				truename=mixname.substring(0,mixname.indexOf('"'));
				truecomment=alinearrayns[ii].substring(0,alinearrayns[ii].indexOf('"'));
				trueid="https://www.facebook.com/"+mixid.substring(0, mixid.indexOf('"'));
				 s=truecomment.replaceAll("\\s+","");
				  if(s.contains("加賴")||s.contains("+賴")||s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			    	
			    n =truename;
			    e =truecomment;
			    golcomment=e;
			    
			    commentckip=new FileWriter("commentckip.txt",false);
			    CKIP c = new CKIP("140.109.19.104" , 1501, "text48953", "ieliao");
				
				c.setRawText(golcomment.replaceAll("\\s+",""));
				c.send();		
	
				for(Term t : c.getTerm()){
					// System.out.print(t.getTerm()+", ");
					 commentckip.write(t.getTerm()+", ");
			                              }
				//System.out.println("\n");
				commentckip.write("\r\n");
				 commentckip.close();
//------------------------------------------------------------------				 
				getcommentformat(FWS);
//-------------------------------------------------------------------		
				     String[] p={"format.txt","0406trainingdata.txt.model","result.txt"};
				    thetrain1.main(p);
				    fr3 = new FileReader("result.txt");
				    br3 = new BufferedReader(fr3);
				    int codenum=1;
				    while (br3.ready())
					  {
						 line2=br3.readLine().toString();
						 alinearray2=line2.split(" ");
						 for(int i=0;i<alinearray2.length;i++)
						 {
							if(alinearray2[i].compareTo("1.0")==0)
							{
								pos++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"P",trueid);
								codenum++;
								
							}
						
							else if(alinearray2[i].compareTo("-1.0")==0)
							{
								neg++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"N",trueid);
								codenum++;
							}
							
								
						 }      
					  }
				    fr3.close();
				
			   
			 }//全部留言
			        if(pos+neg+zero==0)
			    	{
			        	 line2="";
						  pos=0;
						  neg=0;
						  zero=0;
			    	  continue;
			    	
			    	}
			    posrate=pos*100.0/(pos+neg+zero);
			    negrate=neg*100.0/(pos+neg+zero);
			    zerorate=zero*100.0/(pos+neg+zero);
			    DecimalFormat df = new DecimalFormat("##.00");
			    posrate= Double.parseDouble(df.format(posrate));
			    negrate=Double.parseDouble(df.format(negrate));
			    zerorate=Double.parseDouble(df.format(zerorate));
				 System.out.println("正面:"+pos+"中立:"+zero+"負面:"+neg);
				 
				 if(zerorate>=50)
				 {
					 objnewsid.add(objindex,test.SelectTable(newname));
					 objnews.add(newname);
					 objnewsposrate.add(posrate);
					 objnewsnegrate.add(negrate);
					 
					 objindex++;
				 }
				 else if(posrate>negrate&&(posrate-negrate)>10)
				 {
					 possql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.setn.com"+urls.get(time).toString());
				
					 
				 }
				 else if(negrate>posrate&&(negrate-posrate)>10)
				 {
					 negsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.setn.com"+urls.get(time).toString());
				
					 
				 }
				 else
				 {
					 objsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://www.setn.com"+urls.get(time).toString());
				
				 }

					
				 line2="";
				 Arrays.fill(alinearray2, (String) "" );
				  pos=0;
				  neg=0;
				  zero=0;
					 
					
					
		}

		page++;
		posnewsid.clear();
		posnews.clear();
		posnewsposrate.clear();
		posnewsnegrate.clear();
		
		negnewsid.clear();
		negnews.clear();
		negnewsposrate.clear();
		negnewsnegrate.clear();
		
		objnewsid.clear();
		objnews.clear();
		objnewsposrate.clear();
		objnewsnegrate.clear();
	}
	}
	public void runudnhot() throws IOException,DocumentException,Exception//有26頁待爬 
	{
		
	/*	int time=0;//用於迴圈作為計數
		Facebook facebook = new FacebookFactory().getInstance();//以下4行程式碼作為存取FQL的權限(用來取得FB留言)
		facebook.setOAuthAppId("0","0");                            
		facebook.setOAuthPermissions("0");
		facebook.setOAuthAccessToken(new AccessToken("CAACEdEose0cBAGBAax5UdMWdFXJZBpJ91PbqqChXWA9rzn0l1z2yAFDrClXzXKwMywsfoMZAlsZAm8AZAvvZB2FRhLZCU5mreY7xqdqRruQP89OztzbkEi5s40fSqi3QNGGwVcZBry3ZCbfzUYElrKrak17FKmv28s3ZAzjJeNKdiE3Nex4utpQdg5LDiDKLoXopn5Vgv3ZB4F5gZDZD", null));
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		
		Document xmlDoc =  Jsoup.connect("http://udn.com/rank/pv/2/0")
		           .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
		           .get();
		Elements title = xmlDoc.select("a");
		
        
		for (Element e :title) {
		try{
			if(!(e.attr("href").substring(0,11).equals("/news/story")))
				continue;
		    titles.add(e.text());
		    urls.add(e.attr("href"));
		    System.out.println(e.text());
		   // break;
		   }catch(StringIndexOutOfBoundsException rrr)
		{
			continue;   
		}
		
		}

		//System.out.println(titles.get(1));
		//System.out.println(urls.get(0));
//-----------------------------------------------------------------------------
		
		
		
		for(time=0;time<60;time++)//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
			FileWriter out = new FileWriter("allinstring.txt",false);//將留言全部合在一起成為一個string
			FileWriter outrestore=new FileWriter("afterrestore.txt",true);
			
			
			System.out.println("--------------《"+titles.get(time)+"》--------------");//新聞標題
			//outrestore.write("--------------《"+titles.get(time)+"》--------------");
			//outrestore.write("\r\n");
			JSONArray jsonArray2 =new JSONArray();//facebook4j所提供的class
			
			   jsonArray2 = facebook.executeFQL("SELECT  comments_fbid FROM link_stat WHERE url ='http://udn.com"+urls.get(time)+"'");
			   //爬取其他新聞的網址則沒有此問題
			
			JSONObject jsonObject2 = jsonArray2.getJSONObject(0);//用來取得該留言板的ID
			System.out.println(jsonObject2.get("comments_fbid"));
			String query = "SELECT text FROM comment WHERE object_id='"+jsonObject2.get("comments_fbid")+"'";
			JSONArray jsonArray = facebook.executeFQL(query);//將ID及抓取留言版的語法輸入至facebook FQL執行取得
			
			
			//outrestore.write("--------------《"+title.get(time+titlediff).text()+"》--------------");
			//outrestore.write("\r\n");
			//outrestore.close();
			String s;
			if(jsonArray.length()==0)
			{
				continue;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject jsonObject = jsonArray.getJSONObject(i);
			    s=jsonObject.get("text").toString().replaceAll("\\s+","");
			    s=s.toString().replaceAll("\"+","'" );
			    s=s.toString().replaceAll("、","." );
			    if(s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			   // System.out.println(s);
			    out.write("、");
			    out.write(s);
			    
			}
			out.close();
			outrestore.close();
			FileReader fr = new FileReader("allinstring.txt");
			BufferedReader br = new BufferedReader(fr);
			 String content;
			try{
			 content=br.readLine().toString();//將此string丟入CKIP
			
			 HttpCKIP HttpCKIP = new HttpCKIP();
		     Content a=new Content(HttpCKIP.getCKIPByHTTP(content));//輸出afterrestore.txt(已還原一則一則留言(已斷詞過))
			}catch(NullPointerException e)
			{
				continue;
			}
		
		}*/
		ArrayList<Integer> posnewsid=new ArrayList<Integer>();
     	ArrayList<String> posnews=new ArrayList<String>();
     	ArrayList<Double> posnewsposrate=new ArrayList<Double>(); 
     	ArrayList<Double> posnewsnegrate=new ArrayList<Double>();
		
		ArrayList<Integer> negnewsid=new ArrayList<Integer>();
		ArrayList<String> negnews=new ArrayList<String>();
		ArrayList<Double> negnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> negnewsnegrate=new ArrayList<Double>(); 
		
		ArrayList<Integer> objnewsid=new ArrayList<Integer>();
		ArrayList<String> objnews=new ArrayList<String>(); 
		ArrayList<Double> objnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> objnewsnegrate=new ArrayList<Double>();
		
		int posindex=0,objindex=0,negindex=0;
		int getcount=0;
		int page=1;
		while(page<11)//11
	{		
		int time=0;//用於迴圈作為計數

		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> categories = new ArrayList<String>();
		URL url;//jsoup中提供的CLASS
		
		Document xmlDoc =  Jsoup.connect("http://udn.com/rank/pv/2/0/"+page)
		           .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
		           .get();
		
		Elements title = xmlDoc.select("td");
        
		
		int datenamecode=0;
		for (Element e :title) {
				datenamecode++;
				if((datenamecode-2)%6==0)
				{
					 titles.add(e.text());
				     urls.add(e.select("a").attr("href"));
					 System.out.println(e.text());
					 System.out.println(e.select("a").attr("href"));
				}
				if((datenamecode-3)%6==0)
				{
					categories.add(e.text());	
				}
				if((datenamecode-4)%6==0)
				{
					dates.add(e.text());	
				}
				
				//System.out.println(e.text());
			     // System.out.println(e.select("a").attr("href"));
			   // System.out.println(e.select("td.only_1280").text());
			   // break;
			}
//-------------------------------------------------------------------------------
		ArrayList<combination> FWS=new ArrayList<combination>();
		FWS=getfws();
           
//------------------commentckip宣告----------------------		 
	     FileWriter commentckip;
//-------------------------------------------		
	    svm_predict thetrain1=new svm_predict();
	    FileReader fr3 ;//讀取predict結果
		 BufferedReader br3 ;
//------------------------------------------------
		sqlcomment anewsql;
//-------------------------清空區-----------------------↓		
	
		
		
		 int j=1;//for初始 不用再清空
		 String line2;//讀取predict結果整行(也就只有一行)
		 String[] alinearray2 = null;//將每則留言的判斷結果存入陣列
		 double pos=0.0,neg=0.0,zero=0.0;
		 double posrate=0.0,negrate=0.0,zerorate=0.0;
         String n,e;
         
 		sqlclass possql = new sqlclass("positive");
 		//possql.truncateTable();
 		sqlclass negsql = new sqlclass("negative");
 		//negsql.truncateTable();
 		sqlclass objsql = new sqlclass("neutral");
 		//objsql.truncateTable();
//-----------------------------------------------------------------------------
		 newlist test = new newlist();
		for(time=0;time<55;time++)//55//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
		
			String newname;
            newname=titles.get(time).toString();
			
			if(test.SelectTable(newname)!=999999)
			{
				System.out.println("新聞重複-------跳至下篇");
				continue;
			}
			System.out.println("--------------《"+titles.get(time).toString()+"》--------------");//新聞標題
			
			
			anewsql=new sqlcomment();  //產生留言資料庫(用以存該新聞留言)
			
	
			
	
			
			
			
		
			
		
			String s;
		
			
			
			URL urlns = null;
		//Reader reader = null;
		BufferedReader brns = null;

		HttpURLConnection httpConnns = null;
		
		urlns = new URL("http://graph.facebook.com/comments?id=" 
				+ URLEncoder.encode("http://udn.com"+urls.get(time), "UTF8"));
		httpConnns = (HttpURLConnection) urlns.openConnection();
		httpConnns.setConnectTimeout(3 * 60000);
		httpConnns.setReadTimeout(3 * 60000);
		try{
		brns = new BufferedReader(new InputStreamReader(httpConnns.getInputStream()));
		}catch(IOException ee)
		{
			continue;
		}
		
		
		String strns;
		String[] alinearrayns = null;
		String[] alinearray2ns = null;
		
		
		
			
			JSONObject jsonns = new JSONObject(brns.readLine());
			strns=jsonns.toString().replace("\\n",",");
			alinearrayns=strns.split("\"message\":\"");
			alinearray2ns=strns.split("\"from\"");
			
			if(alinearrayns.length<10)
				continue;
			if(getcount>=10)
				return;
			getcount++;
			test.insertTable(categories.get(time).toString(),titles.get(time).toString(),"http://udn.com"+ urls.get(time).toString(),"udn","2016-"+dates.get(time).substring(0,2)+"-"+dates.get(time).substring(3,5));
			for(int ii=1;ii<alinearrayns.length;ii++)
			 {
				String mixname,truename,truecomment,mixid="",trueid="";
				mixname=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('n')).substring(7);
				mixid=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('i')).substring(5);
				truename=mixname.substring(0,mixname.indexOf('"'));
				truecomment=alinearrayns[ii].substring(0,alinearrayns[ii].indexOf('"'));
				trueid="https://www.facebook.com/"+mixid.substring(0, mixid.indexOf('"'));
				 s=truecomment.replaceAll("\\s+","");
				  if(s.contains("加賴")||s.contains("+賴")||s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			    	
			    n =truename;
			    e =truecomment;
			    golcomment=e;
			    
			    commentckip=new FileWriter("commentckip.txt",false);
			    CKIP c = new CKIP("140.109.19.104" , 1501, "text48953", "ieliao");
				
				c.setRawText(golcomment.replaceAll("\\s+",""));
				c.send();		
	
				for(Term t : c.getTerm()){
					// System.out.print(t.getTerm()+", ");
					 commentckip.write(t.getTerm()+", ");
			                              }
				//System.out.println("\n");
				commentckip.write("\r\n");
				 commentckip.close();
//------------------------------------------------------------------				 
				getcommentformat(FWS);
//-------------------------------------------------------------------		
				     String[] p={"format.txt","0406trainingdata.txt.model","result.txt"};
				    thetrain1.main(p);
				    fr3 = new FileReader("result.txt");
				    br3 = new BufferedReader(fr3);
				    int codenum=1;
				    while (br3.ready())
					  {
						 line2=br3.readLine().toString();
						 alinearray2=line2.split(" ");
						 for(int i=0;i<alinearray2.length;i++)
						 {
							if(alinearray2[i].compareTo("1.0")==0)
							{
								pos++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"P",trueid);
								codenum++;
								
							}
						
							else if(alinearray2[i].compareTo("-1.0")==0)
							{
								neg++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"N",trueid);
								codenum++;
							}
							
								
						 }      
					  }
				    fr3.close();
				
			   
			 }//全部留言
			        if(pos+neg+zero==0)
			    	{
			        	 line2="";
						// Arrays.fill(alinearray2, (String) "" );
						  pos=0;
						  neg=0;
						  zero=0;
			    	  continue;
			    	
			    	}
			    posrate=pos*100.0/(pos+neg+zero);
			    negrate=neg*100.0/(pos+neg+zero);
			    zerorate=zero*100.0/(pos+neg+zero);
			    DecimalFormat df = new DecimalFormat("##.00");
			    posrate= Double.parseDouble(df.format(posrate));
			    negrate=Double.parseDouble(df.format(negrate));
			    zerorate=Double.parseDouble(df.format(zerorate));
				 System.out.println("正面:"+pos+"中立:"+zero+"負面:"+neg);
				 
				 if(zerorate>=50)
				 {
					 objsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://udn.com"+ urls.get(time).toString());
				
				 }
				 else if(posrate>negrate&&(posrate-negrate)>10)
				 {
					 possql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://udn.com"+ urls.get(time).toString());

				 }
				 else if(negrate>posrate&&(negrate-posrate)>10)
				 {
					 negsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://udn.com"+ urls.get(time).toString());
					
					
				 }
				 else
				 {
					 objsql.insertTable(test.SelectTable(newname),newname,posrate,negrate,"http://udn.com"+ urls.get(time).toString());
				 }

					
				 line2="";
				 Arrays.fill(alinearray2, (String) "" );
				  pos=0;
				  neg=0;
				  zero=0;
					 
					
					
		}

		page++;
		posnewsid.clear();
		posnews.clear();
		posnewsposrate.clear();
		posnewsnegrate.clear();
		
		negnewsid.clear();
		negnews.clear();
		negnewsposrate.clear();
		negnewsnegrate.clear();
		
		objnewsid.clear();
		objnews.clear();
		objnewsposrate.clear();
		objnewsnegrate.clear();
	
      }
		
	}
	public void segment2() throws Exception  
	{
		ArrayList<Integer> posnewsid=new ArrayList<Integer>();
     	ArrayList<String> posnews=new ArrayList<String>();
     	ArrayList<Double> posnewsposrate=new ArrayList<Double>(); 
     	ArrayList<Double> posnewsnegrate=new ArrayList<Double>();
		
		ArrayList<Integer> negnewsid=new ArrayList<Integer>();
		ArrayList<String> negnews=new ArrayList<String>();
		ArrayList<Double> negnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> negnewsnegrate=new ArrayList<Double>();
		
		ArrayList<Integer> objnewsid=new ArrayList<Integer>();
		ArrayList<String> objnews=new ArrayList<String>(); 
		ArrayList<Double> objnewsposrate=new ArrayList<Double>(); 
		ArrayList<Double> objnewsnegrate=new ArrayList<Double>();
		
		int posindex=0,objindex=0,negindex=0;
		int time=0;//用於迴圈作為計數
		int getcount=0;
		URL url;//jsoup中提供的CLASS
		url = new URL(URL);//藍字URL為新聞RSS
		Document xmlDoc =  Jsoup.parse(url, 3000);//使用Jsoup jar 去解析網頁
		
		Elements title = xmlDoc.select(titletag); //要解析的tag元素為title
		Elements link = xmlDoc.select(webtag);//解析title連結

//-------------------------------------------------------------------------------
		ArrayList<combination> FWS=new ArrayList<combination>();
		FWS=getfws();
           
//------------------commentckip宣告----------------------		 
	     FileWriter commentckip;
//-------------------------------------------		
	    svm_predict thetrain1=new svm_predict();
	    FileReader fr3 ;//讀取predict結果
		 BufferedReader br3 ;
//------------------------------------------------
		sqlcomment anewsql;
//-------------------------清空區-----------------------↓		
	
		
		
		 int j=1;//for初始 不用再清空
		 String line2;//讀取predict結果整行(也就只有一行)
		 String[] alinearray2 = null;//將每則留言的判斷結果存入陣列
		 double pos=0.0,neg=0.0,zero=0.0;
		 double posrate=0.0,negrate=0.0,zerorate=0.0;
         String n,e;
         
 		sqlclass possql = new sqlclass("positive");
 		//possql.truncateTable();
 		sqlclass negsql = new sqlclass("negative");
 		//negsql.truncateTable();
 		sqlclass objsql = new sqlclass("neutral");
//-----------------------------------------------------------------------------
		 newlist test = new newlist();
		for(time=1;time<35;time++)//由於不同新聞網所以提供的新聞個數不同，所以在此取這三間新聞網所提供的數量最小值為20，故每個新聞網皆能爬取20則新聞
		{	
			URL thisurl;//jsoup中提供的CLASS
			thisurl = new URL(link.get(time+webdiff).text().substring(0, 70));//藍字URL為新聞RSS
			Document xmlDoc2 =  Jsoup.parse(thisurl, 3000);//使用Jsoup jar 去解析網頁	
			Element content2 = xmlDoc2.getElementById("summary");
			Elements p2= content2.getElementsByTag("p"); //要解析的tag元素為title
			
			//爬新聞內容
			String newname;
			if(title.get(time+titlediff).text().isEmpty()||link.get(time+webdiff).text().isEmpty()||title.get(time+titlediff).text().substring(0, 7).equals("《TAIPEI"))
			break;//由於ltn新聞網的RSS最後幾則是提供國際英文的新聞，故當掃到新聞標題為英文時就停止爬取
			

			
			if(test.SelectTable(title.get(time+titlediff).text())!=999999)
			{
				System.out.println("新聞重複-------跳至下篇");
				continue;
			}
			
			System.out.println("--------------《"+title.get(time+titlediff).text()+"》--------------"+link.get(time+webdiff).text().substring(54,58)+"-"+link.get(time+webdiff).text().substring(58,60)+"-"+link.get(time+webdiff).text().substring(60,62));//新聞標題
			
			newname=title.get(time+titlediff).text();
			anewsql=new sqlcomment();  //產生留言資料庫(用以存該新聞留言)
			
			
			
			
			
			
			String s;

	
			URL urlns = null;
		//Reader reader = null;
		BufferedReader brns = null;

		HttpURLConnection httpConnns = null;
		
		urlns = new URL("http://graph.facebook.com/comments?id=" 
				+ URLEncoder.encode(link.get(time+webdiff).text().substring(0,70), "UTF8"));
		httpConnns = (HttpURLConnection) urlns.openConnection();
		httpConnns.setConnectTimeout(3 * 60000);
		httpConnns.setReadTimeout(3 * 60000);
		try{
		brns = new BufferedReader(new InputStreamReader(httpConnns.getInputStream()));
		}catch(IOException ee)
		{
			continue;
		}
		
		
		String strns;
		String gategoryapple="";
		String[] alinearrayns = null;
		String[] alinearray2ns = null;
		
		
		
			
			JSONObject jsonns = new JSONObject(brns.readLine());
			strns=jsonns.toString().replace("\\n",",");
			alinearrayns=strns.split("\"message\":\"");
			alinearray2ns=strns.split("\"from\"");
			
			if(alinearrayns.length<10)
				continue;
			if(getcount>=10)
			    return;
			getcount++;
			Document xmlDoc3 =  Jsoup.connect(link.get(time+webdiff).text().substring(0, 70))
			           .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
			           .get();
			
			Elements category = xmlDoc3.getElementsByTag("script");
	        
			
			
			for (Element element :category ){                
		        for (DataNode node : element.dataNodes()) {
		            
		            if(node.getWholeData().contains("keywords"))
		            {
		            	gategoryapple=node.getWholeData().substring(node.getWholeData().indexOf("keywords")+13,node.getWholeData().indexOf("keywords")+15);
		            	
		            	
		            }
		        }
		     //   System.out.println("-------------------");            
		  }
			test.insertTable(gategoryapple,title.get(time + titlediff).text(), link.get(time+webdiff).text().substring(0,70),"appledaily",link.get(time+webdiff).text().substring(54,58)+"-"+link.get(time+webdiff).text().substring(58,60)+"-"+link.get(time+webdiff).text().substring(60,62));
			for(int ii=1;ii<alinearrayns.length;ii++)
			 {
				String mixname,truename,truecomment,mixid="",trueid="";
				mixname=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('n')).substring(7);
				mixid=alinearray2ns[ii].substring(alinearray2ns[ii].indexOf('i')).substring(5);
				truename=mixname.substring(0,mixname.indexOf('"'));
				truecomment=alinearrayns[ii].substring(0,alinearrayns[ii].indexOf('"'));
				trueid="https://www.facebook.com/"+mixid.substring(0, mixid.indexOf('"'));

				 s=truecomment.replaceAll("\\s+","");
				  if(s.contains("加賴")||s.contains("+賴")||s.contains("Line")||s.contains("LINE")||s.contains("line")||s.contains("儲值")||s.contains("http")||s.contains("goo.gl")||s.contains("加賴")||s.contains("看照約妹")||s.contains("茶坊"))
			    	continue;
			    	
			    n =truename;
			    e =truecomment;
			    golcomment=e;
			    
			    commentckip=new FileWriter("commentckip.txt",false);
			    CKIP c = new CKIP("140.109.19.104" , 1501, "text48953", "ieliao");
				
				c.setRawText(golcomment.replaceAll("\\s+",""));
				c.send();		
	
				for(Term t : c.getTerm()){
					// System.out.print(t.getTerm()+", ");
					 commentckip.write(t.getTerm()+", ");
			                              }
				//System.out.println("\n");
				commentckip.write("\r\n");
				 commentckip.close();
//------------------------------------------------------------------				 
				getcommentformat(FWS);
//-------------------------------------------------------------------		
				     String[] p={"format.txt","0406trainingdata.txt.model","result.txt"};
				    thetrain1.main(p);
				    fr3 = new FileReader("result.txt");
				    br3 = new BufferedReader(fr3);
				    int codenum=1;
				    while (br3.ready())
					  {
						 line2=br3.readLine().toString();
						 alinearray2=line2.split(" ");
						 for(int i=0;i<alinearray2.length;i++)
						 {
							if(alinearray2[i].compareTo("1.0")==0)
							{
								pos++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"P",trueid);
								codenum++;
								
							}
						
							else if(alinearray2[i].compareTo("-1.0")==0)
							{
								neg++;
								anewsql.insertTable(test.SelectTable(newname),n,e,"N",trueid);
								codenum++;
							}
							
								
						 }      
					  }
				    fr3.close();
				
			   
			 }//全部留言
			        if(pos+neg+zero==0)
			    	{
			        	 line2="";
						// Arrays.fill(alinearray2, (String) "" );
						  pos=0;
						  neg=0;
						  zero=0;
			    	  continue;
			    	
			    	}
			    posrate=pos*100.0/(pos+neg+zero);
			    negrate=neg*100.0/(pos+neg+zero);
			    zerorate=zero*100.0/(pos+neg+zero);
			    DecimalFormat df = new DecimalFormat("##.00");
			    posrate= Double.parseDouble(df.format(posrate));
			    negrate=Double.parseDouble(df.format(negrate));
			    zerorate=Double.parseDouble(df.format(zerorate));
				 System.out.println("正面:"+pos+"中立:"+zero+"負面:"+neg);
				 
				 if(zerorate>=50)
				 {

					 objsql.insertTable(test.SelectTable(newname),title.get(time+titlediff).text(),posrate,negrate,link.get(time+webdiff).text().substring(0,70));
				 }
				 else if(posrate>negrate&&(posrate-negrate)>10)
				 { 
					 possql.insertTable(test.SelectTable(newname),title.get(time+titlediff).text(),posrate,negrate,link.get(time+webdiff).text().substring(0,70) );
				 }
				 else if(negrate>posrate&&(negrate-posrate)>10)
				 {
					 negsql.insertTable(test.SelectTable(newname),title.get(time+titlediff).text(),posrate,negrate,link.get(time+webdiff).text().substring(0,70));
				 }
				 else
				 {
					 objsql.insertTable(test.SelectTable(newname),title.get(time+titlediff).text(),posrate,negrate,link.get(time+webdiff).text().substring(0,70));
				 }

					
				 line2="";
				 Arrays.fill(alinearray2, (String) "" );
				  pos=0;
				  neg=0;
				  zero=0;
					 
					
					
		}
		
		
	}

}

