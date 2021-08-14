<!DOCTYPE HTML>



<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    
    	<link type="text/css" rel="stylesheet" href="stylesheet.css"/>
    	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

		<title>新聞標題</title>

	</head>

	<body>

    <nav class="navbar navbar-custom navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-main-collapse">
                    <i class="fa fa-bars"></i>
                </button>
                <a class="navbar-brand page-scroll" href="#page-top" style="color:#1d9b6c">
                    <span class="light">歡迎使用</span> 熱門新聞之留言情緒分析系統
                </a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse navbar-right navbar-main-collapse">
                <ul class="nav navbar-nav">
                    <!-- Hidden li included to remove active class from about link when scrolled up past about section -->
                    <li class="hidden">
                        <a href="#page-top"></a>
                    </li>
                    <li>
                        <a class="page-scroll" href="#about" style="color:#1d9b6c">設計理念</a>
                    </li>
                    <li>
                        <a class="page-scroll" href="#download" style="color:#1d9b6c">實驗流程及方法</a>
                    </li>
                    <li>
                        <a class="page-scroll" href="#category" style="color:#1d9b6c">情緒分類</a>
                    </li>
                    <li>
                        <a class="page-scroll" href="#contact" style="color:#1d9b6c">實驗室&指導教授</a>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>

		<table border=10 align=center width=100%>

		<tr>
  			<th>新聞名稱</th>
        <th>來源</th>
  			<th>比率</th>
 		</tr>

		<?php 
  			$link=mysql_connect("140.120.15.148","printer","printer3d");  //建立資料連線
  			mysql_select_db("emergeapp",$link);  //開啟資料庫
  			$sql="select news_id,name,class,list_news.source from positive,list_news where positive.news_id = list_news.id"; //查詢資料
  			mysql_set_charset('utf8', $link);
  			$result=mysql_query($sql,$link);  //執行查詢動作
  			$number_of_rows=mysql_num_rows($result);  //取出查詢結果

  			while (list($news_id,$name,$class,$source)=mysql_fetch_row($result)) {
  	 			echo(" <tr>\n".
          			"  <td><a href=\"comment.php?id=".$news_id."\">$name</a></td>\n".
                "  <td>$source</td>\n".
                "  <td>$class</td>\n".
          			" </tr>\n");
  }


  mysql_close($link);
 ?>
		</table>



		

		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	</body>

</html>