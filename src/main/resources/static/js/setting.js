$(function () {
    $("#uploadFrom").submit(upload);
});

function upload() {
    $.ajax({
        url:"http://upload-z2.qiniup.com",
        method:"post",
        //是否将表单内容转化为字符串
        processData:false,
        //不让jQuery去设置上传类型，浏览器会自动会设置
        contentType:false,
        //传的数据，需要特殊处理一下使用FormData，js对象，传入产生from对象，但是$("#uploadFrom")这个是jQuery选择器，得到是jQuery对象，要的是js,jQuery是它的数组
        data:new FormData($("#uploadFrom")[0]),
        success:function (data) {
            if(data &&data.code==0){
                //更新头像
                $.post(
                  CONTEXT_PATH+"/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function (data) {
                        data=$.parseJSON(data);
                        if(data.code==0){
                            window.location.reload();
                        }else {
                            alert(data.msg);
                        }
                    }
                );
            }else {
                alert("上传失败");
            }
        }
    })
    //表单默认是做了一些事情，通过action，但是我们这里采用的方法是没有action，所有当我们执行完这个函数就让他停止，不去执行默认操作
    return false;
}