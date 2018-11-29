function submitLogin() {
        var userCode = $.trim($("#userCode").val());
        var userPwd = $.trim($("#userPwd").val());
        var hexPwd = hex_md5(userPwd);
        $.ajax({
            type: 'POST',
            //url: "http://localhost:8080/auth/login_in",
            url: "http://localhost:8080/auth/toLogin",
            data: {"userCode":userCode,"userPwd":hexPwd},
            dataType:"json",
            success: function(rep){
                if(rep.status == 0){
                    //alert(rep.message);
                    window.location.href="http://localhost:8080/auth/index";
//                    loadPage(0,baseUrl);
                }else{
                    alert(rep.message);
                }
            },
            error:function(rep){
                alert(rep);
                alert("获取信息失败!");
            }
        });
}