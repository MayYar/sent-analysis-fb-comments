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

public class newlist {

	private Connection con = null;
	private Statement stat = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;

	private String truncatedbSQL = "TRUNCATE TABLE list_news " ;
	
	private String insertdbSQL = "insert into list_news(id,category,title,url,source,date) "
			+ "select ifNULL(max(id),0)+1 ,?,?,?,?,? FROM list_news"; // 把變化的資料設為問號 ; // 把變化的資料設為問號

	private String selectSQL = "select id from list_news where title = " ;
	

	public newlist() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// 註冊driver
			con = DriverManager
					.getConnection(
							 "jdbc:mysql://140.120.15.148/emergeapp?useUnicode=true&characterEncoding=Big5",
							 "printer", "printer3d");

		} catch (ClassNotFoundException e) {
			System.out.println("DriverClassNotFound :" + e.toString());
		}// 有可能會產生sqlexception
		catch (SQLException x) {
			System.out.println("Exception :" + x.toString());
		}
		
		
		

	}

	// 新增資料
	// 可以看看PrepareStatement的使用方式
	public void insertTable(String category,String title, String url,String source,String timestamp) {
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				// 註冊driver
				con = DriverManager
						.getConnection(
								 "jdbc:mysql://140.120.15.148/emergeapp?useUnicode=true&characterEncoding=Big5",
								 "printer", "printer3d");

			} catch (ClassNotFoundException e) {
				System.out.println("DriverClassNotFound :" + e.toString());
			}// 有可能會產生sqlexception
			catch (SQLException x) {
				System.out.println("Exception :" + x.toString());
			}
			pst = con.prepareStatement(insertdbSQL);

			pst.setString(1, category);
			pst.setString(2, title);
			pst.setString(3, url);
			pst.setString(4, source);
			pst.setString(5, timestamp);
			pst.executeUpdate();
		} catch (SQLException e) {
			System.out.println("InsertDB Exception :" + e.toString());
		} finally {
			
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
	public int SelectTable(String newtitle) throws SQLException {
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				// 註冊driver
				con = DriverManager
						.getConnection(
								 "jdbc:mysql://140.120.15.148/emergeapp?useUnicode=true&characterEncoding=Big5",
								 "printer", "printer3d");

			} catch (ClassNotFoundException e) {
				System.out.println("DriverClassNotFound :" + e.toString());
			}// 有可能會產生sqlexception
			catch (SQLException x) {
				System.out.println("Exception :" + x.toString());
			}
			
			stat = con.createStatement();
			rs = stat.executeQuery(selectSQL+"'"+newtitle+"'");
			rs.first();
			return rs.getInt("id");
			 
		} catch (SQLException e) {
			//System.out.println("DropDB Exception :" + e.toString());
			return 999999;
			
		} finally {
			
			Close();
			
		}
		//return rs.getInt("id");
		
	}

	// 完整使用完資料庫後,記得要關閉所有Object
	// 否則在等待Timeout時,可能會有Connection poor的狀況
	private void Close() {
		try {
			con.close();
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
		} catch (SQLException e) {
			System.out.println("Close Exception :" + e.toString());
		}
		finally
		{
            rs = null;
			
			stat = null;
			
			pst = null;
			
			
		}
	}

	
}
