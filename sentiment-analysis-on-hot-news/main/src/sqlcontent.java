import java.net.*; 
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class sqlcontent {

	private Connection con = null;
	private Statement stat = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;
	private PreparedStatement PSUpdate = null;
	private String truncatedbSQL ;
	
	private String insertdbSQL ; // 把變化的資料設為問號

	private String selectSQL ;
	private String createdbSQL ;
	private String updateSQL ; 

	public sqlcontent() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// 註冊driver
			con = DriverManager
					.getConnection(
							 "jdbc:mysql://localhost/text48953?useUnicode=true&characterEncoding=Big5",
							"text48953", "ieliao");

		} catch (ClassNotFoundException e) {
			System.out.println("DriverClassNotFound :" + e.toString());
		}// 有可能會產生sqlexception
		catch (SQLException x) {
			System.out.println("Exception :" + x.toString());
		}
		
		truncatedbSQL = "TRUNCATE TABLE comment";
		insertdbSQL = "insert into newscontent(new_id,content) "
				+ "values(?,?)";
		selectSQL = "select * from comment";
		createdbSQL = "CREATE TABLE comment (" + 
			    "    name     VARCHAR(50)CHARACTER SET utf8 NULL " +
			    "  , comment     VARCHAR(1000)CHARACTER SET utf8 NULL " +
			    "  , rate    VARCHAR(50)CHARACTER SET utf8 NULL " + 
			    "  , code    VARCHAR(50)CHARACTER SET utf8 NULL " + 
			    " )"; 
		

	}

	// 新增資料
	// 可以看看PrepareStatement的使用方式
	public void insertTable(int id,String content) {
		try {
			pst = con.prepareStatement(insertdbSQL);

			pst.setInt(1, id);
			pst.setString(2, content);

		
			pst.executeUpdate();
		} catch (SQLException e) {
			System.out.println("InsertDB Exception :" + e.toString());
		} finally {
			Close();
		}
	}
	public void createTable() 
	  { 
	    try 
	    { 
	      stat = con.createStatement(); 
	      stat.executeUpdate(createdbSQL); 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("CreateDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 

	public void truncateTable() 
	  { 
	    try 
	    { 
	      stat = con.createStatement(); 
	      stat.executeUpdate(truncatedbSQL); 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("DropDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
	
	// 查詢資料
	// 可以看看回傳結果集及取得資料方式
	public void SelectTable() {
		try {
			stat = con.createStatement();
			rs = stat.executeQuery(selectSQL);
			/*
			 * System.out.println("ID\t\tName\t\tPASSWORD"); while(rs.next()) {
			 * System.out.println(rs.getInt("id")+"\t\t"+
			 * rs.getString("name")+"\t\t"+rs.getString("passwd")); }
			 */
		} catch (SQLException e) {
			System.out.println("DropDB Exception :" + e.toString());
		} finally {
			Close();
		}
	}
	
	public void updateTable(String code,String emotion) 
	  { 
		
	    try 
	    { 
	      updateSQL = "update comment set rate = ? where code = ?";
	      PSUpdate = con.prepareStatement(updateSQL);
          PSUpdate.setString(1,emotion);
          PSUpdate.setString(2,code);
          PSUpdate.executeUpdate();
          
	     
	    } 
	    catch(SQLException e) 
	    { 
	    	System.err.println("Got an exception! ");
	        System.err.println(e.getMessage());
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 

	// 完整使用完資料庫後,記得要關閉所有Object
	// 否則在等待Timeout時,可能會有Connection poor的狀況
	private void Close() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stat != null) {
				stat.close();
				stat = null;
			}
			if (pst != null) {
				pst.close();
				pst = null;
			}
			if(PSUpdate!=null){
				PSUpdate.close();
				PSUpdate=null;
			}
		} catch (SQLException e) {
			System.out.println("Close Exception :" + e.toString());
		}
	}

	
}
