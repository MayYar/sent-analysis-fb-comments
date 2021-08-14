<!DOCTYPE HTML>



<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    
    	<link type="text/css" rel="stylesheet" href="stylesheet.css"/>
    	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

		<title>新聞標題</title>

    <style>
    table {
        border-collapse: collapse;
        width: 80%;
        margin-top:50px;
    }

    th,td {
        padding: 8px;
        text-align: center;
        border-bottom: 1px solid #ddd;
        font-family: 微軟正黑體;
    }

    tr:hover{background-color:#f5f5f5}

    img{
      margin-right:20px;
    }
    </style>

	</head>

	<body>

  <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <a class="navbar-brand"><font color="#1d9b6c" face="malgun gothic",simhei>歡迎使用熱門新聞事件之情緒分析系統</font></a>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                    
                    <li>
                        <a href="rank.php"><font color="#00E8D0" face="Yu Gothic", SimHei>婉君排行榜</font></a>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>
  
  <div style="margin-top:100px" align="center">
   
        <a href="\project\udn.php"><img src="\project\img\udn.jpg" width="150px" height="60px" align="center"></a>
    
   
        <a href="\project\appledaily.php"><img src="\project\img\appledaily.jpg" width="150px" height="60px" align="center"></a>
    
        <a href="\project\ltn.php"><img src="\project\img\ltn.jpg"width="150px" height="60px" align="center"></a>
  
        <a href="\project\setn.php"><img src="\project\img\setn.jpg" width="150px" height="60px" align="center"></a>
   
        <a href=""><img src="\project\img\ettoday.jpg" width="150px" height="60px" align="center"></a>
    
  </div>


  <div  align="center">

		<table>

		<tr>
        <th>類別</th>
  			<th>新聞名稱</th>
        <th>日期</th>
 		</tr>

		<?php 
  			$link=mysql_connect("140.120.15.148","printer","printer3d");  //建立資料連線
  			mysql_select_db("emergeapp",$link);  //開啟資料庫
  			$sql="select negative.news_id,negative.name,list_news.date,list_news.category from negative,list_news  
              where negative.news_id = list_news.id and list_news.source = 'udn'
              order by date desc"; //查詢資料
  			mysql_set_charset('utf8', $link);
  			$result=mysql_query($sql,$link);  //執行查詢動作
  			$number_of_rows=mysql_num_rows($result);  //取出查詢結果

  			while (list($news_id,$name,$date,$category)=mysql_fetch_row($result)) {
  	 			echo(" <tr>\n".
                "  <td>$category</td>\n".
          			"  <td><a href=\"comment.php?id=".$news_id."\">$name</a></td>\n".
                "  <td>$date</td>\n".
          			" </tr>\n");
  }


  mysql_close($link);
 ?>
		</table>
  </div>


		

		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	</body>

</html>