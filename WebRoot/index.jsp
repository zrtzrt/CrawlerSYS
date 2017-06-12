<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>CrawerSYS</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="//cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
	<script src="http://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>
	<script src="//cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/content/styles.css">
	<script src="${pageContext.request.contextPath}/content/xpath.js"></script>
	<link href="${pageContext.request.contextPath}/content/bootstrap-switch.min.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/content/bootstrap-switch.min.js"></script>
    <script src="${pageContext.request.contextPath}/content/script.js"></script>
  </head>
  
  <body>
    <div id="top">
    	<h1 id="title">
    		<span style="color: #00CCFF">Crawler</span>
    		<span style="color: #66FF99">S</span>
    		<span style="color: #99FFCC">Y</span>
    		<span style="color: #66FF99">S</span>
    		<span style="color: #CC66CC">V0.2.3</span>
    		<span style="color: #CC6666">BATE</span>
    	</h1>
	</div>
	<div id="left">
		<%@ include file="/pages/browser.jsp" %>
	</div>
	<div id="right">
		<%@ include file="/pages/setting.jsp" %>
	</div>
	<script type="text/javascript">
	$(function() { 
	$(".switch").bootstrapSwitch({  
        onText:"启动",  
        offText:"停止",  
        onColor:"success",
        offColor:"info",
        size:"small",
        onSwitchChange:function(event,state){
            event.target.value=state;
      }
	});
	$("#xpath").bootstrapSwitch({  
        onText:"启动",  
        offText:"停止",  
        onColor:"success",  
        offColor:"info",  
        size:"small",  
        onSwitchChange:function(event,state){
            if(state==true){  
            	getXpath();
            }else{  
                $("#brower").off();
           }  
      }  
	});
	
            $('.input-group-url').initInputGroup({  
                'widget' : 'input', //输入框组中间的空间类型 
                'field': 'url',
                'btn':'Open(打开)',
                'btn_cb': openUrl
            });
            $('.input-group-xpath').initInputGroup({  
                'widget' : 'input', //输入框组中间的空间类型 
                'field': 'xpath',
                'field2': 'lable'
            });
            $('.input-group-node').initInputGroup({  
                'widget' : 'input', //输入框组中间的空间类型 
                'field': 'node',
                'btn':'Ping(测试连接)',
                'btn_cb': testCon
            }); 
            
   
	});
</script> 
  </body>
</html>
