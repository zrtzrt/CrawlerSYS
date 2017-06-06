(function($){
        $.fn.getQuery = function(options){
                o = $.extend({
                        type: 'xpath',        //生成类型  'xpath' or 'selector'
                        highLight : true, //选择的元素是否高亮显示
                        fullPath : false, //是否是全路径
                        preferenceAttr: 'id', //属性偏好 'id' or 'class'
                        bgColor: 'yellow',        //背景色
                        border:'yellow 1px solid',//边框
                        expansion: 3,//扩大边框
                        container:'body'
                }, options||{});
                if(o.highLight){
                	$("div").remove(".highLight");
                        this.highLight(o);
                }
                
                var path = getPath(this, '')+"/text()";
                path=path.replaceAll("//div[@id='brower']", "/");
                selector = path.replaceAll('/', '>').replaceAll('\\[', ':eq(').replaceAll('\\]', ')').replaceAll('\\:eq\\(\\@', '[').replaceAll('\'\\)', '\']');
                query = '/' + path;
                if(!o.fullPath){
                        query = '/' + query;
                }
                if(o.type=='selector'){
                        return selector;
                } else {
                        return query;
                }
        }


        this.getXpath = function(){
                return query;
        }


        this.getSelector = function(){
                return selector;
        }


        $.fn.highLight = function (options){
                op = $.extend({
                        bgColor: 'yellow',        //背景色
                        border:'yellow 1px solid',//边框
                        expansion: 3,//扩大边框
                        container:'body'
                }, options||{});
                $(op.container).append("<div id='abs-box' class='highLight'> </div>");
                $('head').append("<style>.abs{position:absolute;zoom:1;pointer-events:none;z-index:-1;}</style>");
                var div = $('#abs-box');
                if(div != this){
                        var pos=this.position(), em = op.expansion;
                        div.css({'left':pos.left-em,'top':pos.top-em,'width':this.width()+2*em,'height':this.height()+2*em});
                        div.css({'background-color':op.bgColor, 'border':op.border });
                }
        }
                
        function getPath (e, path){
                var tn = e.get(0).tagName;
                if(isNullOrEmpty(e) || isNullOrEmpty(tn)){
                        return path;
                }
                var attr = getAttr(e);
                tn = tn.toLowerCase() + attr;
                path = isNullOrEmpty(path) ? tn : tn + "/" + path;
                var parentE = e.parent();
                if(isNullOrEmpty(parentE) || (!o.fullPath && attr.substring(0, 5) == '[@id=')){
                        return path;
                }
                return getPath(parentE, path);
        }


        function getAttr (e){
                var tn = e.get(0).tagName;
                var id = e.attr('id'), clazz = e.attr('class');
                var hasId = !isNullOrEmpty(id), hasClazz = !isNullOrEmpty(clazz);
                id = "[@id='" + id + "']";
                clazz = "[@class='" + clazz + "']";
                if(hasId && hasClazz){
                        if(o.preferenceAttr.toLowerCase() == 'class'){
                                return clazz;
                        } else {
                                return id;
                        }
                } else if(hasId && !hasClazz) {
                        return id;
                } else if(!hasId && hasClazz) {
                        return clazz;
                } else {
                        if(e.siblings(tn).size() > 0) {
                                var i = e.prevAll(tn).size();
                                if(o.type=='xpath'){
                                        i++;
                                }
                                return '[' + i + ']';
                        } else {
                                return '';
                        }
                }
        }


        function isNullOrEmpty (o){
                return null==o || 'null'==o || ''==o || undefined==o;
        }


})(jQuery);


String.prototype.replaceAll = function(regx,t){   
        return this.replace(new RegExp(regx,'gm'),t);   
};


//$(document).ready(function () {
// $(document).click(function(e){
//         e=e||window.event;
//         var target=e.target||e.srcElement;
//         var path = $(target).getQuery({
//             type: 'xpath',        //生成类型:  'xpath' or 'selector', 默认是'xpath'
//             preferenceAttr: 'class', // 属性偏好: 'id' or 'class', 默认是id
//             highLight : true, //选择的元素是否高亮显示, 默认是true
//             bgColor: 'yellow',        //背景色, 默认是'yellow'
//             border:'yellow 1px solid',//边框, 默认是'yellow 1px solid'
//             expansion: 3,//扩大边框, 默认是3
//             fullPath: false //是否是全路径, 默认是false
//         });
//         alert(path);
//         return false;
//     });
//});