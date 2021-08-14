<!DOCTYPE HTML>



<html>

	<head>
		<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">


    <script src="js/d3.v3.min.js" language="JavaScript"></script>
    <script src="js/liquidFillGauge.js" language="JavaScript"></script>
    <style>
        .liquidFillGaugeText { font-family: Helvetica; font-weight: bold; }
    </style>
    
    <script type="text/javascript" src="//code.jquery.com/jquery-1.12.0.min.js"></script>
		<script type="text/javascript">
            $(document).ready(function(){
                $("#fillgauge2").click(function(){
                $("#pos").fadeToggle("slow");
                $("#neg").hide();
                });
            
            
             
            $("#fillgauge3").click(function() {
                $("#neg").fadeToggle("slow");
                $("#pos").hide();
              }); 
            });
    </script>
    
    <title>News Display</title>

	</head>

	<body>

  <div class="container" align = center>

    <?php
        $link=mysql_connect("140.120.15.148","printer","printer3d");  //建立資料連線
        mysql_select_db("emergeapp",$link);  //開啟資料庫
        mysql_set_charset('utf8', $link);

        $sql2="SELECT url FROM list_news WHERE ".$_GET["id"]."= list_news.id ";
        $result2=mysql_query($sql2,$link);//執行查詢動作
        $number_of_rows=mysql_num_rows($result2);//取出查詢結果
        while (list($url)=mysql_fetch_row($result2)) {echo ("<iframe src=\"".$url."\" width=100% height=695></iframe>");}
		?>
  </div> 

    


  <!--*******************正面液態球*******************-->
  <div class="row">
    <div class="col-xs-6 col-sm-6" align=center>
    <svg id="fillgauge2" width="30%" height="200" ></svg>
      
      <?php
        $link=mysql_connect("140.120.15.148","printer","printer3d");  //建立資料連線
        mysql_select_db("emergeapp",$link);  //開啟資料庫
        mysql_set_charset('utf8', $link);

        $sql3="SELECT posrate FROM negative WHERE ".$_GET["id"]."= negative.news_id ";
        $result3=mysql_query($sql3,$link);//執行查詢動作
        $number_of_rows=mysql_num_rows($result3);//取出查詢結果
        
    
        if (list($posrate)=mysql_fetch_row($result3)) {
          echo (
        
            "<script language=\"JavaScript\">".
            "var config1 = liquidFillGaugeDefaultSettings();".
            "config1.circleColor = \"#FF7777\";".
            "config1.textColor = \"#FF4444\";".
            "config1.waveTextColor = \"#FFAAAA\";".
            "config1.waveColor = \"#FFDDDD\";".
            "config1.circleThickness = 0.2;".
            "config1.textVertPosition = 0.2;".
            "config1.waveAnimateTime = 1000;".
            "loadLiquidFillGauge(\"fillgauge2\", $posrate, config1);".
            "</script>");
        }
        mysql_close($link);
      ?>

    </div>
  



<!--*******************負面液態球*******************-->
    <div class="col-xs-6 col-sm-6" align=center>
        <svg id="fillgauge3" width="30%" height="200" ></svg>
      
      <?php
        $link=mysql_connect("140.120.15.148","printer","printer3d");  //建立資料連線
        mysql_select_db("emergeapp",$link);  //開啟資料庫
        mysql_set_charset('utf8', $link);

        $sql3="SELECT negrate FROM negative WHERE ".$_GET["id"]."= negative.news_id ";
        $result3=mysql_query($sql3,$link);//執行查詢動作
        $number_of_rows=mysql_num_rows($result3);//取出查詢結果
        
    
        if (list($negrate)=mysql_fetch_row($result3)) {
          echo (
        
            "<script language=\"JavaScript\">".
            "var config2 = liquidFillGaugeDefaultSettings();".
            "config2.circleColor = \"#D4AB6A\";".
            "config2.textColor = \"#553300\";".
            "config2.waveTextColor = \"#805615\";".
            "config2.waveColor = \"#AA7D39\";".
            "config2.circleThickness = 0.1;".
            "config2.circleFillGap = 0.2;".
            "config2.textVertPosition = 0.8;".
            "config2.waveAnimateTime = 2000;".
            "config2.waveHeight = 0.3;".
            "config2.waveCount = 1;".
            "loadLiquidFillGauge(\"fillgauge3\", $negrate, config2);".
            "</script>");
        }
        mysql_close($link);
      ?>
    </div>
  


</div>
<!--正面留言-->
<div class="row">
<div id="pos" style="display:none">
  <table border=10 align=center width=80%>

    <tr>
        <th width=10%>USER</th>
        
        <th>COMMENT</th>
        <th>CLASS</th>
        
    </tr>
    <?php 
        $link=mysql_connect("140.120.15.148","printer","printer3d");  //建立資料連線
        mysql_select_db("emergeapp",$link);  //開啟資料庫
        mysql_set_charset('utf8', $link);

        $sql="SELECT user, comment, class FROM comments WHERE ".$_GET["id"]."= comments.news_id AND comments.class ='P' ";
        $result=mysql_query($sql,$link);  //執行查詢動作
        
        
       while (list($user,$comment,$class)=mysql_fetch_row($result)) {
          echo(" <tr>\n".
                "  <td align=center>$user</td>\n".
                "  <td>$comment</td>\n".
                "  <td align=center>$class</td>\n".
                " </tr>\n");
        }


        mysql_close($link);
    ?> 
  </table>
</div>
 </div>     
<!--負面留言-->
<div class="row">
<div id="neg" style="display:none">
  <table border=10 align=center width=80%>

    <tr>
        <th width=10%>USER</th>
        
        <th>COMMENT</th>
        <th>CLASS</th>
        
    </tr>
    <?php 
        $link=mysql_connect("140.120.15.148","printer","printer3d");  //建立資料連線
        mysql_select_db("emergeapp",$link);  //開啟資料庫
        mysql_set_charset('utf8', $link);

        $sql="SELECT user, comment, class FROM comments WHERE ".$_GET["id"]."= comments.news_id AND comments.class ='N' ";
        $result=mysql_query($sql,$link);  //執行查詢動作
        
        
       while (list($user,$comment,$class)=mysql_fetch_row($result)) {
          echo(" <tr>\n".
                "  <td align=center>$user</td>\n".
                "  <td>$comment</td>\n".
                "  <td align=center>$class</td>\n".
                " </tr>\n");
        }


        mysql_close($link);
    ?> 
  </table>
</div>		

</div>

<hr>
<footer>
            <div class="row">
                <div class="col-lg-12">
                    <p>Copyright © Your Website 2015</p>
                </div>
            </div>
            <!-- /.row -->
        </footer>
		

		<!-- jQuery -->
    <script src="js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.min.js"></script>

    <!-- Plugin JavaScript -->
    <script src="js/jquery.easing.min.js"></script>

    <!-- Google Maps API Key - Use your own API key to enable the map feature. More information on the Google Maps API can be found at https://developers.google.com/maps/ -->
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCRngKslUGJTlibkQ3FkfTxj3Xss1UlZDA&sensor=false"></script>

    <!-- Custom Theme JavaScript -->
    <script src="js/grayscale.js"></script>

</html>