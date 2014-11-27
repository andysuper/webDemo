<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript" src="style/operamasks-ui-2.0/js/jquery.min.js"></script>
<script type="text/javascript" src="style/operamasks-ui-2.0/js/operamasks-ui.min.js"></script>
<script type="text/javascript" src="style/zTree/js/jquery.ztree.core-3.5.js"></script>

<link href="style/operamasks-ui-2.0/css/apusic/om-apusic.css" rel="stylesheet" type="text/css" />
<link href="style/zTree/css/zTreeStyle/zTreeStyle.css" rel="stylesheet" type="text/css" />
<link href="style/css/common.css" rel="stylesheet" type="text/css" />

<script type="text/javascript">
	$(document).ready(function() {
		borderLayout();
		loadTreeData();
	});

	//页面布局
	function borderLayout() {
		$('#content').omBorderLayout({
			panels : [ {
				id : "bleft",
				title : "",
				region : "west",
				resizable : true,
				collapsible : true,
				width : 200
			}, {
				id : "acontent",
				title : "",
				region : "center",
				width : 1200
			} ],
			spacing : 3
		});
	}

	//初始化树的各配置项
	var zTreeObj, zTreeNodes, setting = {
		view : {
			selectedMulti : false
		},
		callback : {
			onClick : zTreeOnClick
		}
	};
	
	//加载树的数据
	function loadTreeData() {
		$.ajax({
			type : "POST",
			data : {
				name : 'andy'
			},
			url : "info_findAllInfo.do",
			success : function(data) {
				zTreeNodes = eval("(" + data + ")");
				zTreeObj = $.fn.zTree.init($("#tree"), setting, zTreeNodes);
			}
		});
	}

	//树节点点击事件
	function zTreeOnClick() {
		console.log(0);
	}
	
	function openOrFold(t){
		if($(t).val()=='+'){
			zTreeObj.expandAll(true);
		}else{
			zTreeObj.expandAll(false);
		}
	}
</script>
<style type="text/css">
</style>
</head>
<body>
	<div id="content" style="width: 1400px; height: 900px;">
		<div id="bleft">
			<div class="tree_div_open_fold">
				<input type="button" value="+" title="展开全部" onclick="openOrFold(this)"/>
				<input type="button" value="-" title="折叠全部" onclick="openOrFold(this)"/>
			</div>
			<ul id="tree" class="ztree"></ul>
		</div>
		<div id="acontent" style="width: 100%; padding: 0;">
			<div class="bcontent_1">
				<div class="cell_01">
					<form action="" name="from1" method="post" id="paginateForm">
						
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>