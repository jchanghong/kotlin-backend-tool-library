package com.github.jchanghong.http

import cn.hutool.core.util.URLUtil

fun main() {
	val docoenment = """
        <!DOCTYPE html>
        <html>

        <head>
        	<meta charset="UTF-8" />
        	<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1, minimum-scale=1">
        	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
        	<meta name="_csrf_header" content="X-CSRF-TOKEN" />
        	<meta name="_isHashHistory" content="" />
        	<title></title>
        	<meta name="_context" content="/xmap-web">

        	<!-- freemarker begin -->
        	<meta name="_language" content="zh_CN" />
        	<meta name="_csrf" content="df154cae-14e8-4e2f-af4e-05e2d8306e42" />
        	<script type="text/javascript"  src="https://55555.1.172.2:443/hgis-web/hgis.js"></script>
        	<link id="hgisCSS" rel="stylesheet"  href="https://55555.1.172.2:443/hgis-web/gisapi/theme/hgis.css"/>
        	<!-- freemarker end -->

        	<!--[if lte IE 8]>
        	<script>
        		if(typeof window!=='undefined'){
        			var matchMediaPolyfill=function matchMediaPolyfill(){
        				return{matches:false,addListener:function addListener(){},removeListener:function removeListener(){}};
        			};
        			window.matchMedia=window.matchMedia||matchMediaPolyfill;
        		}

        		document.getElementsByTagName('html')[0].className = 'ie8';
        		var context = document.getElementsByName("_context")[0].getAttribute("content");
        		var path=document.location+"";
        		path= path.substr(0,path.indexOf(context,path.indexOf("//")+2)+context.length);
        		document.getElementsByName("_context")[0].setAttribute("content",path);
        	</script>
        	<script type="text/javascript" src="/xmap-web/resources/static/js/excanvas.compiled.js"></script>
        	<![endif]-->
        <link href="/xmap-web/resources/vendor.css" rel="stylesheet"/></head>

        <body>
        <link rel="stylesheet" href="/xmap-web/resources/style.css"/>
        <!--[if lte IE 8]>
        <link rel="stylesheet" href="/xmap-web/resources/style-ie8.css"/>
        <![endif]-->
        <div id="app"></div>
        <object style="display: none;" classid='clsid:4D409E23-A845-46DC-9FE9-8AA243B7CB33' id='HikToolCheckOCX' width='0' height='0' name='HikToolCheckOCX'></object>
        <script type="text/javascript" src="/xmap-web/resources/scripts/manifest.js?3138d17dc42d734ccc17"></script><script type="text/javascript" src="/xmap-web/resources/scripts/vendor.js?3138d17dc42d734ccc17"></script><script type="text/javascript" src="/xmap-web/resources/scripts/index.js?3138d17dc42d734ccc17"></script></body>

        </html>
    """.trimIndent()
//    println(HttpHelper.getCsrfNameAndValueFromHtml(docoenment, chain))

	val url1 = "https://55555.1.172.2/portal/login/ajax/verifyCode.do"
	val url2 = "https://55555.1.172.2:80/portal/login/ajax/verifyCode.do"
	println(URLUtil.getPath(url1).removePrefix("/").substringBefore("/"))
	println(URLUtil.getPath(url2).removePrefix("/").substringBefore("/"))
	println(HttpHelper.getFirstPath(url1))
}
