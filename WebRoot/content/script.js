
function getXpath() {
//openUrl("baidu.cn");
var bg;
$("#brower").on("mouseover",function(e) { 
         e=e||window.event;
         var target=e.target||e.srcElement;
         bg = $(target).css("background-color");
         $(target).css({'background-color':'yellow'});
         });
$("#brower").on("mouseout",function(e) { 
         e=e||window.event;
         var target=e.target||e.srcElement;
         $(target).css({'background-color':bg});
         });
$("#brower").on("click",function(e) { 
         e=e||window.event;
         var target=e.target||e.srcElement;
         var path = $(target).getQuery({
         	highLight:false,
         	container:'#brower'
         });
         alert(path);
         var order = $(".input-group-xpath").children(".input-group").length;
         $("#autoinput_xpath_"+order).val(path);
         $("#autoBTN_xpath_"+order).trigger("click");
         return false;
         });
}

function getUrl(url){
	var re = new RegExp("^((https|http)?://){1}")
	url = re.test(url)?url:"http://"+url;
	return url;
}

function openUrl(url){
    if($("#proxy").val){
    	$("#brower").load("CrawlerServlet?method=get&url="+getUrl(url));
    	if($("#banJs").val)
    		$("#brower").find("script").remove();
    }else
    	$("#brower").empty().append($("<div class=\"embed-responsive embed-responsive-4by3\"><iframe id=\"brower\" class=\"embed-responsive-item\" src="+url+"></iframe></div>"));
}

function testCon(url,d) {
	ping(url.split(':')[0],function(ping){d.html(ping);});
}

function ping(url,cb) 
{
var ping, requestTime, responseTime ;
$.ajax({
    url: getUrl(url)+'/'+window.location.href.substring(window.location.protocol.length+window.location.host.length+2,window.location.href.length),  //设置一个空的ajax请求
    type: 'GET',
    dataType: 'html',
    timeout: 10000,
    beforeSend : function() 
    {
        requestTime = new Date().getTime();
    },
    complete : function() 
    {
        responseTime = new Date().getTime();
        ping = Math.abs(requestTime - responseTime);
        if(cb) cb(ping);
    },
    error : function() 
    {
        if(cb) cb("连接错误");
    }
});
}
var json = new Object();
json={"urls":[],"xpaths":[],"lables":[],"nodes":[]};
function collectData(){
	var err="";
	var t="";
	$(".input-group-url").children().find('input').each(function (i){
		t=$(this).val();
		if(t.length>0)
			json.urls[i]={"url":t};
		else if(i==1)
			if($("#setting-auto").val()!=true)
				err+="url 不可为空\n";
	});
	$(".input-group-xpath").children().find('input').each(function (i){
		if(i%2==0){
			t=$(this).val();
			if(t.length>0)
				json.xpaths[i/2]={"xpath":t};
			else if(i==1)
				err+="xpath 不可为空\n";
		}else json.lables[(i-1)/2] = {"lable":$(this).val()};
//		}else json.lables[(i-1)/2].lable = $(this).val();
	});
	$(".input-group-node").children().find('input').each(function (i){
		json.nodes[i] = {"node":$(this).val()};
//		json.nodes[i].node = $(this).val();
	});
	t = $("#setting-thread").val();
	if(t.length>0)
		json.thread=t;
	t = $("#setting-limit").val();
	if(t.length>0)
		json.limit=t;
	t = $("#setting-sleep").val();
	if(t.length>0)
		json.sleep=t;
	if($("#setting-save").val()){
//		alert("#setting-save"+$("#setting-save").val());
		t=$("#setting-dbip").val();
		if(t.length>0)
			json.dbip=t;
		else err+="dbip 不可为空\n";
		t=$("#setting-user").val();
		if(t.length>0)
			json.user=t;
		else err+="user 不可为空\n";
		t=$("#setting-password").val();
		if(t.length>0)
			json.pw=t;
		else err+="password 不可为空\n";
		t=$("#setting-table").val();
		if(t.length>0)
			json.table=t;
		else err+="table 不可为空\n";
		
	}
	if($("#setting-auto").val()){
//		alert("#setting-auto"+$("#setting-auto").val());
		json.then="auto";
	}
	else
		if($("#setting-save").val())
			json.then=$("#setting-then").val();
	if($("#setting-recur").val()){
//		alert("#setting-recur"+$("#setting-recur").val());
		json.relink=true;
		t=$("#setting-link").val();
		if(t.length>0)
			json.link=t;
	}
	t = $("#setting-interval").val();
	if(t.length>0)
		json.interval=t;
	else{
		t = $("#setting-timer").val();
		if(t.length>0)
			json.timer=t;
	}
	if(err.length>0){
		alert(err);
		return;
	}else
	var r=confirm("please confirm(请确认)：\n"+JSON.stringify(json, null, 4));
	if (r==true)
		openSocket();
}
var websocket;
function openSocket(){
	if(websocket!=null){
		send(JSON.stringify(json));
		return;
	}
//	$.ajax({
//		url:"CrawlerServlet?method=startSocket"
//	});
	websocket = new WebSocket("ws://"+window.location.hostname+":6544/"); 
    websocket.onopen = function(evt) { 
        $("#left").load("CrawlerServlet?method=console");
        send(JSON.stringify(json));
    }; 
    websocket.onclose = function(evt) { 
    	var li = $("<li class='list-group-item list-group-item-warning'></li>");
    	li.append("off connection(断开连接)");
        $("#console-list").append(li);
    }; 
    websocket.onmessage = function(evt) { 
        showMessage(evt.data);
    }; 
    websocket.onerror = function(evt) { 
    	var li = $("<li class='list-group-item list-group-item-danger'></li>");
    	li.append(evt.data);
        $("#console-list").append(li);
    };
}
function send(message) {  
	waitForConnection(function () {  
		websocket.send(message);
	}, 1000);  
};  
function waitForConnection(callback, interval) {  
	if (websocket.readyState === 1) {  
		callback();  
	} else {
		// optional: implement backoff for interval here  
		setTimeout(function () {  
			waitForConnection(callback, interval);  
		}, interval);  
	}  
};
function ArrayList(){ 
	 this.arr=[], 
	 this.size=function(){ 
		 return this.arr.length; 
	 }, 
	 this.add=function(){ 
		 if(arguments.length==1){ 
			 this.arr.push(arguments[0]); 
		 }else if(arguments.length>=2){ 
			 var deleteItem=this.arr[arguments[0]]; 
			 this.arr.splice(arguments[0],1,arguments[1],deleteItem);
		 } 
		 return this; 
	 }, 
	 this.get=function(index){ 
		 return this.arr[index]; 
	 }, 
	 this.removeIndex=function(index){ 
		 this.arr.splice(index,1); 
	 }, 
	 this.removeObj=function(obj){ 
		 this.removeIndex(this.indexOf(obj)); 
	 }, 
	 this.indexOf=function(obj){ 
		 for(var i=0;i<this.arr.length;i++){ 
			 if (this.arr[i]==obj) { 
				 return i; 
			 }; 
		 } 
		 return -1; 
	 }, 
	 this.isEmpty=function(){ 
		 return this.arr.length==0; 
	 }, 
	 this.clear=function(){ 
		 this.arr=[]; 
	 }, 
	 this.contains=function(obj){ 
		 return this.indexOf(obj)!=-1; 
	 };
}
var donelist = new ArrayList();
function showMessage(msg){
	if(msg){
//		alert(msg);
		var m = JSON.parse(msg);
		if(m.done!=null){
			if(donelist.indexOf(m.ip)==-1){
				donelist.add(m.ip);
				var li = $("<li id='nodeInfo-"+m.ip+"' class='list-group-item'></li>");
				var head = $("<h3>node(节点进度)："+m.ip+"</h3>");
				var bar = $("<div class='progress'></div>");
				var finsh = $("<div id='doneber-"+m.ip+
						"' class='progress-bar progress-bar-striped active' role='progressbar' style='width:"
						+m.done+"'>"+m.done+"</div>");
				bar.append(finsh);
				li.append(head).append(bar);
				$("#console-list").append(li);
			}else{
				$("#doneber-"+m.ip).css("width",m.done+"%").html(m.done+"%");
			}
		}else if(m.finsh){
			$("#doneber-"+m.ip).css("width","100%").html("100%");
			var li = $("<li class='list-group-item list-group-item-success'>节点："+m.ip+"completed(已完成)</li>");
			$("#console-list").append(li);
		}else if(m.newUrl){
			var li = $("<li class='list-group-item list-group-item-info'>节点："+m.ip+"执行"+m.newUrl+"个新链接</li>");
			$("#console-list").append(li);
		}
	}
}
function openServer(){
	$.ajax({
		url:"CrawlerServlet?method=startServer"
	});
}