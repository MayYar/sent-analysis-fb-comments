package main;

import java.sql.SQLException;
import java.util.ArrayList;

import tw.cheyingwu.ckip.CKIP;
import tw.cheyingwu.ckip.Term;

public class similartest {

	public static void main(String[] args) throws SQLException {
		
		ArrayList<String> uniterm = new ArrayList<String>();
		ArrayList<String> news = new ArrayList<String>();
		similarcheck test = new similarcheck();
		String sample="黃國昌爆走立院";
		
		String allnews="";
		news=test.SelectTable("2016-04-29");
		for(int i=0;i<news.size();i++)
		{
			allnews=allnews+news.get(i);	
		}
		System.out.println(allnews);

		int check=0;
		
		
		    CKIP c = new CKIP("140.109.19.104" , 1501, "text48953", "ieliao");
			
			c.setRawText(allnews);
			c.send();		

			for(Term t : c.getTerm())
			{
				if(t.getTerm().equals("？")||t.getTerm().equals("：")||t.getTerm().equals("...")||t.getTerm().equals("！")||t.getTerm().equals("　")||t.getTerm().equals("「")||t.getTerm().equals("」")||t.getTerm().equals("【")||t.getTerm().equals("】")||t.getTerm().equals("／")||"的".equals(t.getTerm())||"是".equals(t.getTerm())||"一".equals(t.getTerm())||"在".equals(t.getTerm())||"有".equals(t.getTerm())
						||"個".equals(t.getTerm())||"我".equals(t.getTerm())||"這".equals(t.getTerm())||"了".equals(t.getTerm())||"他".equals(t.getTerm())||"也".equals(t.getTerm())||"就".equals(t.getTerm())||"人".equals(t.getTerm())||"都".equals(t.getTerm())||"說".equals(t.getTerm())||"而".equals(t.getTerm())||"我們".equals(t.getTerm())||"你".equals(t.getTerm())||"了".equals(t.getTerm())||"要".equals(t.getTerm())
						||"之".equals(t.getTerm())||"及".equals(t.getTerm())||"和".equals(t.getTerm())||"與".equals(t.getTerm())||"以".equals(t.getTerm())||"很".equals(t.getTerm())||"種".equals(t.getTerm())||"中".equals(t.getTerm())||"大".equals(t.getTerm())||"著".equals(t.getTerm())||"她".equals(t.getTerm())||"那".equals(t.getTerm())||"上".equals(t.getTerm())||"但".equals(t.getTerm())||"年".equals(t.getTerm())||"還".equals(t.getTerm())
						||"時".equals(t.getTerm())||"最".equals(t.getTerm())||"自己".equals(t.getTerm())||"為".equals(t.getTerm())||"來".equals(t.getTerm())||"所".equals(t.getTerm())||"他們".equals(t.getTerm())||"兩".equals(t.getTerm())||"各".equals(t.getTerm())||"上".equals(t.getTerm())||"或".equals(t.getTerm())||"等".equals(t.getTerm())||"又".equals(t.getTerm())||"將".equals(t.getTerm())||"因為".equals(t.getTerm())||"於".equals(t.getTerm())
						||"由".equals(t.getTerm())||"從".equals(t.getTerm())||"更".equals(t.getTerm())||"被".equals(t.getTerm())||"才".equals(t.getTerm())||"已".equals(t.getTerm())||"者".equals(t.getTerm())||"每次".equals(t.getTerm())||"把".equals(t.getTerm())||"三".equals(t.getTerm())||"甚麼".equals(t.getTerm())||"其".equals(t.getTerm())||"讓".equals(t.getTerm())||"此".equals(t.getTerm())||"做".equals(t.getTerm())||"在".equals(t.getTerm())
						||"所以".equals(t.getTerm())||"只".equals(t.getTerm())||"則".equals(t.getTerm())||"卻".equals(t.getTerm())||"地".equals(t.getTerm())||"並".equals(t.getTerm())||"位".equals(t.getTerm())||"得".equals(t.getTerm())||"想".equals(t.getTerm())||"去".equals(t.getTerm())||"呢".equals(t.getTerm())||"學生".equals(t.getTerm())||"表示".equals(t.getTerm())||"公司".equals(t.getTerm())||"到".equals(t.getTerm())||"將".equals(t.getTerm()))
					continue;
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
			System.out.println(uniterm);
			
			
			
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
			c.setRawText(news.get(j));
			c.send();
			for(Term t : c.getTerm())
			{
			   
			   for(int i=0;i<uniterm.size();i++)
				{
					if(t.getTerm().equals(uniterm.get(i)))
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
			
				
		}
			
	}

}
