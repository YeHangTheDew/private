# private
这是一个springboot的实践项目

#实现多数据源

#使用durid

#对默认的thymeleaf进行了解
var contextPath = '${pageContext.request.getContextPath()}';
相比上面的jsp,thymeleaf是这样获取到项目地址的
<script type="text/javascript" th:inline="javascript">
       /*<![CDATA[*/
       ctxPath = /*[[@{/}]]*/ '';
       /*]]>*/
 
       console.info(ctxPath);
</script>

#日志实现采用logback

#使用分页插件：pagehelper

#mybatis自动生成mybatis-generator

#redis实现基本的单点登录功能

#shiro权限框架
