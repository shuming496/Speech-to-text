package org.xianlv.stt;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextArea;

import org.xianlv.stt.response.HttpResponse;
import org.xianlv.stt.response.TextResult;
import org.xianlv.stt.utils.HttpUtil;
import org.xianlv.stt.utils.RequestBody;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;

/**
 * @author smc
 *
 */
public class STT {

	// 原始音频存放地址
	private String input_file = "";

	private JTextArea info_message;

	/**
	 * STT服务url
	 */
	// 阿里云语音服务地址
	private static String url = "https://nlsapi.aliyun.com/transcriptions";
	private static RequestBody body = new RequestBody();
	private static HttpUtil request = new HttpUtil();

	STT(String default_inpath_value, JTextArea info_message) {
		input_file = default_inpath_value.replace("\\", "\\\\");
		this.info_message = info_message;
	}

	/**
	 * 文件上传到Oss
	 * 
	 * @return
	 */
	public String uploadFileToOss() {
		// Oss
		// endpoint以北京为例，其它region请按实际情况填写
		String endpoint = "http://oss-cn-beijing.aliyuncs.com";
		// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录
		// https://ram.console.aliyun.com 创建
		String accessKeyId = "";
		String accessKeySecret = "";

		// 获取文件名称
		String key = input_file.substring(input_file.lastIndexOf("\\") + 1);
		// 输出进度至界面
		info_message.append("当前正在转写 [" + key + "]... \n");
		info_message.paintImmediately(info_message.getBounds());

		// 创建OSSClient实例
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

		// 上传文件
		PutObjectResult objectResult = ossClient.putObject("audiototext3", key, new File(input_file));

		Date expiration = new Date(new Date().getTime() + 3600 * 1000);// 生成URL
		URL url = ossClient.generatePresignedUrl("audiototext3", key, expiration);

		// 关闭client
		ossClient.shutdown();
		return url.toString();
	}

	/**
	 * 调用阿里云语音转文本服务
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public TextResult audioToText(String fileLink) {
		
		String ak_id = ""; // 数加管控台获得的accessId
		String ak_secret = ""; // 数加管控台获得的accessSecret
		
		// 返回结果对象，
		TextResult result = new TextResult();
		List<String> list = new ArrayList<String>();

		body.setApp_key("nls-service-multi-domain"); // 简介页面给出的Appkey
		body.setFile_link(fileLink);// 离线文件识别的文件url,推荐使用oss存储文件。链接大小限制为128MB

		// 热词接口
		// 使用热词需要指定Vocabulary_id字段，如何设置热词参考文档：[热词设置](~~49179~~)
		// body.setVocabulary_id("vocab_id");

		/* 获取完整识别结果，无需设置本参数！ */
		// body.addValid_time(100,2000,0); //validtime 可选字段
		// 设置的是语音文件中希望识别的内容,begintime,endtime以及channel
		// body.addValid_time(2000,10000,1); //validtime 默认不设置。可选字段
		// 设置的是语音文件中希望识别的内容,begintime,endtime以及channel
		/* 获取完整识别结果，无需设置本参数！ */

		System.out.println("Recognize begin!");

		/*
		 * 发送录音转写请求
		 **/
		String bodyString;
		bodyString = JSON.toJSONString(body, true);
		System.out.println("bodyString is:" + bodyString);
		HttpResponse httpResponse = HttpUtil.sendPost(url, bodyString, ak_id, ak_secret);
		if (httpResponse.getStatus() == 200) {
			System.out.println("post response is:" + httpResponse.getResult());
		} else {
			System.out.println("error msg: " + httpResponse.getMessage());
		}

		/*
		 * 通过TaskId获取识别结果
		 **/
		if (httpResponse.getStatus() == 200) {
			String TaskId = JSON.parseObject(httpResponse.getResult()).getString("id");
			String status = "RUNNING";
			HttpResponse getResponse = null;
			while (status.equals("RUNNING")) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				getResponse = HttpUtil.sendGet(url, TaskId, ak_id, ak_secret);
				if (getResponse.getStatus() == 200) {
					status = JSON.parseObject(getResponse.getResult()).getString("status");
					if (status.equals("SUCCEED")) {
						String resultJson = JSON.parseObject(getResponse.getResult()).getString("result");
						List<Map<String, String>> listMaps = (List<Map<String, String>>) JSON.parse(resultJson);

						for (Map<String, String> map : listMaps) {
							list.add(map.get("text"));
						}

						result.setStatus("SUCCESS");
						result.setTexts(list);
						System.out.println("结果数据:" + result.getTexts());
					}
					// System.out.println("get response is:" + getResponse.getResult());
				} else {
					result.setStatus("ERROR");
					list.add(getResponse.getMessage());
					result.setTexts(list);
					System.out.println("error msg: " + getResponse.getMessage());
					break;
				}
			}

			System.out.println("Recognize over!");
		}
		return result;
	}

	public TextResult start() {
		String fileLink = uploadFileToOss();
		return audioToText(fileLink);
	}
}
