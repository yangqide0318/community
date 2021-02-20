$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});
//点击letter，弹出框中的发送，就会触发这个ajax方法
function send_letter() {
	//第一步收起弹出框
	$("#sendModal").modal("hide");
	//第二步获取两个变量
	var toName= $("#recipient-name").val();
	var content=$("#message-text").val();
	//第三步，产生一个异步请求
	$.post(
		//参数一，请求路径
		CONTEXT_PATH+"/message/letter/send",
		//两个变量参数
		{"toName":toName,"content":content},
		//第三参数其实也就是回调函数，当请求完毕后得到返回结果对结果的处理
		//当然中间的data其实就是我们异步请求返回的返回值（这里是一个json格式的String！！不是json对象）
		function (data) {
			//转化，将json格式字符串转化为json对象
			data=$.parseJSON(data);
			//判断执行结果0——执行成功
			if(data.code==0){
				//这里是将提示信息传入到我们的提示框中的hintBody中
				$("#hintBody").text("发送成功！");
			}else{
				$("#hintBody").text(data.msg);
			}
			//显示提示框
			$("#hintModal").modal("show");
			//设置一个时间超过这个时间就进行Handler函数操作
			setTimeout(function(){
				//收起提示框
				$("#hintModal").modal("hide");
				//刷新当前页面
				location.reload();
			}, 2000);
		}
	);

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}