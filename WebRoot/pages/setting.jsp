<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'setting.jsp' starting page</title>
    <script src="${pageContext.request.contextPath}/content/inputGroup.js"></script>
  </head>
  
  <body>
    <div class="panel-group" id="setting">
	<div class="panel panel-success">
		<div class="panel-heading" data-toggle="collapse" data-target="#collapseOne">
			<h4 class="panel-title">
				choose website(选择网页)
			</h4>
		</div>
		<div id="collapseOne" class="panel-collapse collapse in">
			<div class="panel-body">
				<div class="panel panel-default">
  					<div class="panel-heading"  data-toggle="collapse" data-target="#collapseUrl">
   						 <h3 class="panel-title">
							url(网站域名)
						</h3>
  					</div>
  					<div id="collapseUrl" class="panel-collapse collapse in">
  					<div class="panel-body">
						<div class="input-group-url"></div>
  					</div>
  					</div>
				</div>
				<div class="panel panel-default">
  					<div class="panel-heading" data-toggle="collapse" data-target="#collapseXpath">
   						 <h3 class="panel-title">auto(自动正文提取)
   						 <input id="setting-auto" class="switch" type="checkbox"/>
   						 </h3>
  					</div>
  					<div id="collapseXpath" class="panel-collapse collapse in">
  					<div class="panel-body">
						<div class="input-group-xpath"></div>
  					</div>
  					</div>
				</div>
				
			</div>
		</div>
	</div>
	<div class="panel panel-info">
		<div class="panel-heading" data-toggle="collapse" data-target="#collapseTwo">
			<h4 class="panel-title">
				crawler config(配置爬虫)
			</h4>
		</div>
		<div id="collapseTwo" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="input-group">
  					<span class="input-group-addon">thread(线程数)</span>
  					<input id="setting-thread" type="text" class="form-control" placeholder="3">
  					<span class="input-group-addon">limit(限制)</span>
  					<input id="setting-limit" type="text" class="form-control" placeholder="9">
  					<span class="input-group-addon">sleep(间隔)</span>
  					<input id="setting-sleep" type="text" class="form-control" placeholder="500">
				</div>
				<div class="input-group">
				</div>
				<div class="panel-heading"  data-toggle="collapse" data-target="#collapseSave">
   						 <h3 class="panel-title">save(持久化)
   						 <input id="setting-save" class="switch" type="checkbox"/>
   						 </h3>
  					</div>
  					<div id="collapseSave" class="panel-collapse collapse in">
  					<div class="panel-body">
  					<div class="input-group">
  					<span class="input-group-addon">dbip(数据库地址)</span>
  					<input id="setting-dbip" type="text" class="form-control" placeholder="127.0.0.1:3306/*">
  					<span class="input-group-addon">type(保存类型)</span>
  					<select id="setting-then" class="form-control">
  						<option value="save">single data(单值保存)</option>
                        <option value="saveaslist">multi data(多条保存)</option>
  					</select>
				</div>
						<div class="input-group">
  					<span class="input-group-addon">user</span>
  					<input id="setting-user" type="text" class="form-control" placeholder="root">
  					<span class="input-group-addon">password</span>
  					<input id="setting-password" type="password" class="form-control" placeholder="root">
  					<span class="input-group-addon">table</span>
  					<input id="setting-table" type="text" class="form-control" placeholder="table">
				</div>
  					</div>
  					</div>
  					<div class="panel-heading"  data-toggle="collapse" data-target="#collapseRecur">
   						 <h3 class="panel-title">recur(广度爬虫)
   						 <input id="setting-recur" class="switch" type="checkbox"/>
   						 </h3>
  					</div>
  					<div id="collapseRecur" class="panel-collapse collapse in">
  					<div class="panel-body">
						<div class="input-group">
  					<span class="input-group-addon">linkRegex(链接正则)</span>
  					<input id="setting-link" type="text" class="form-control" placeholder="//a/@href">
				</div>
  					</div>
  					</div>
  			</div>
		</div>
	</div>
	<div class="panel panel-warning">
		<div class="panel-heading" data-toggle="collapse" data-target="#collapseThree">
			<h4 class="panel-title">
					add node(添加节点)
			</h4>
		</div>
		<div id="collapseThree" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="panel panel-default">
  					<div class="panel-heading" data-toggle="collapse" data-target="#collapseNode">
   						 <h3 class="panel-title">node(节点地址)</h3>
  					</div>
  					<div id="collapseNode" class="panel-collapse collapse in">
  					<div class="panel-body">
						<div class="input-group-node"></div>
  					</div>
  					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="panel panel-danger">
		<div class="panel-heading">
			<h4 class="panel-title" data-toggle="collapse" data-target="#collapseFour">
				timer run(定时运行)
			</h4>
		</div>
		<div id="collapseFour" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="input-group">
  					<span class="input-group-addon">fixed hours(间隔运行（小时）)</span>
  					<input id="setting-interval" type="text" class="form-control" placeholder="1">
				</div>
				<div class="input-group">
  					<span class="input-group-addon">fixed clock(定时运行)</span>
  					<input id="setting-timer" type="text" class="form-control" placeholder="12:59:59">
				</div>
				<button id="btn-run" class='btn btn-success' type='button' onclick="collectData()">RUN(运行)</button>
				<!--button id="btn-server" class='btn btn-danger' type='button' onclick="openServer()">启动本地服务</button-->
			</div>
		</div>
	</div>
</div>
  </body>
</html>
