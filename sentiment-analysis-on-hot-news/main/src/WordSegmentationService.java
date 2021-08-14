

import java.util.LinkedList;
import java.util.List;

import javax.naming.LinkLoopException;


public class WordSegmentationService {
	protected String rawText;
	protected String returnText;
	protected List<Term> term;
	
	public void setRawText(String rawText){
		this.rawText = rawText;
	}

	public void setReturnText(String returnText){
		this.returnText = returnText;
	}
	
	public String getReturnText(){
		return returnText;
	}
	
	public LinkedList<String> getReturnList(){
		LinkedList<String> list = new LinkedList<String>();
		for(String s:returnText.split("　")){
			list.add(s);
		}
		return list;
	}
}
