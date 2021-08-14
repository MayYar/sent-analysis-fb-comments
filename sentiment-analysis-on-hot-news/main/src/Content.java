import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;




public class Content {
	
	public Content(LinkedList<String> termSet) throws IOException{
		
		FileWriter out=new FileWriter("notrestore.txt",false);
		for(String term:termSet)
		{
			String key = term.replaceAll("(\\S+)\\((\\S+)\\)", "$1");
			String value = term.replaceAll("(\\S+)\\((\\S+)\\)", "$2");
		//-------------------------------------	
		  if(!"".equals(key)&&!"COMMACATEGORY".equals(value)&&!"DASHCATEGORY".equals(value)&&!"ETCCATEGORY".equals(value)&&!"PARENTHESISCATEGORY".equals(value)&&!"PERIODCATEGORY".equals(value)&&!"SEMICOLONCATEGORY".equals(value)&&!"SPCHANGECATEGORY".equals(value)&&!"QUESTIONCATEGORY".equals(value)&&!"EXCLAMATIONCATEGORY".equals(value)
		        	&&!"COLONCATEGORY".equals(value)&&!"FW".equals(value)&&!"SHI".equals(value)&&!"DE".equals(value)&&!"V_2".equals(value)&&!"Caa".equals(value)&&!"Cab".equals(value)&&!"Cba".equals(value)&&!"Da".equals(value)&&!"Di".equals(value)&&!"Neqa".equals(value)&&!"Neqb".equals(value)
		        	&&!"I".equals(value)&&!"T".equals(value)&&!"Nf".equals(value)&&!"Cbb".equals(value)&&!"Neu".equals(value)&&!"P".equals(value)&&!"Nd".equals(value)&&!"Nh".equals(value)&&!"Ncd".equals(value)&&!"的".equals(key)&&!"是".equals(key)&&!"一".equals(key)&&!"在".equals(key)&&!"有".equals(key)
		        	&&!"個".equals(key)&&!"我".equals(key)&&!"這".equals(key)&&!"了".equals(key)&&!"他".equals(key)&&!"也".equals(key)&&!"就".equals(key)&&!"人".equals(key)&&!"都".equals(key)&&!"說".equals(key)&&!"而".equals(key)&&!"我們".equals(key)&&!"你".equals(key)&&!"了".equals(key)&&!"要".equals(key)
		        	&&!"之".equals(key)&&!"及".equals(key)&&!"和".equals(key)&&!"與".equals(key)&&!"以".equals(key)&&!"很".equals(key)&&!"種".equals(key)&&!"中".equals(key)&&!"大".equals(key)&&!"著".equals(key)&&!"她".equals(key)&&!"那".equals(key)&&!"上".equals(key)&&!"但".equals(key)&&!"年".equals(key)&&!"還".equals(key)
		        	&&!"時".equals(key)&&!"最".equals(key)&&!"自己".equals(key)&&!"為".equals(key)&&!"來".equals(key)&&!"所".equals(key)&&!"他們".equals(key)&&!"兩".equals(key)&&!"各".equals(key)&&!"上".equals(key)&&!"或".equals(key)&&!"等".equals(key)&&!"又".equals(key)&&!"將".equals(key)&&!"因為".equals(key)&&!"於".equals(key)
		        	&&!"由".equals(key)&&!"從".equals(key)&&!"更".equals(key)&&!"被".equals(key)&&!"才".equals(key)&&!"已".equals(key)&&!"者".equals(key)&&!"每次".equals(key)&&!"把".equals(key)&&!"三".equals(key)&&!"甚麼".equals(key)&&!"其".equals(key)&&!"讓".equals(key)&&!"此".equals(key)&&!"做".equals(key)&&!"在".equals(key)
		        	&&!"所以".equals(key)&&!"只".equals(key)&&!"則".equals(key)&&!"卻".equals(key)&&!"地".equals(key)&&!"並".equals(key)&&!"位".equals(key)&&!"得".equals(key)&&!"想".equals(key)&&!"去".equals(key)&&!"呢".equals(key)&&!"學生".equals(key)&&!"表示".equals(key)&&!"公司".equals(key)&&!"到".equals(key)&&!"將".equals(key)
		        	)
			out.write(key+", ");
		//---------------------------------	
		}
		   out.close();//斷詞後還沒還原成一則一則留言
		   FileReader fr = new FileReader("notrestore.txt");
		   BufferedReader br = new BufferedReader(fr);
		   FileWriter outrestore=new FileWriter("afterrestore.txt",true);//還原成一則一則留言(已斷詞)
		    String[] alinearray;
		    String line;
            int i;
		    line=br.readLine().toString();
		    alinearray=line.split(", ");
		    for(i=1;i<alinearray.length;i++)
			{
			     if(!alinearray[i].equals("、"))
				  {
						 System.out.print(alinearray[i]+", ");
						 outrestore.write(alinearray[i]+", ");
				   }
				 else 
					{
						 System.out.print("\n");
						 outrestore.write("\r\n");
					 }
			} 
		    outrestore.write("\r\n");	 
				 outrestore.close();
			  
		    
	}

	

}
