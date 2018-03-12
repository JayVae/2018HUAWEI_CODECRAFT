package com.elasticcloudservice.predict;

import java.util.*;

public class Predict {

	public static String[] predictVm(String[] ecsContent, String[] inputContent) {

		/** =========do your work here========== **/

		String[] results = new String[ecsContent.length];

		List<String> history = new ArrayList<String>();

//		input 文件的处理
//		物理服务器CPU核数 内存大小（GB） 硬盘大小（GB）
		int CPUNum = Integer.parseInt(inputContent[0].split(" ")[0]);
		int MENNum = Integer.parseInt(inputContent[0].split(" ")[1]);
		int flavorNum = Integer.parseInt(inputContent[2]);
		String endDate = inputContent[inputContent.length-1].split(" ")[0];
		String startDate = inputContent[inputContent.length-2].split(" ")[0];
		String preType = inputContent[inputContent.length-4];
		Map<String,int[]> vmMap = new HashMap<>();
		for (int i = 3; i < 3+flavorNum; i++) {
			String[] tmp = inputContent[i].split(" ");
			vmMap.put(tmp[0],new int[]{Integer.parseInt(tmp[1]),Integer.parseInt(tmp[1])});
//			Integer.parseInt(inputContent[i])>>10;

		}
		Set keySet = vmMap.keySet();
		Map<String,Integer> vmNum = new HashMap<>();

		if(preType == "CPU"){

		}else{

		}

//		训练数据集的处理
		for (int i = 1; i < ecsContent.length; i++) {

			if (ecsContent[i].contains("\t")
					&& ecsContent[i].split("\t").length == 3) {

				String[] array = ecsContent[i].split("\t");
				String uuid = array[0];
				String flavorName = array[1];
				String createTime = array[2];
				if(keySet.contains(array[0])){

				}

				history.add(uuid + " " + flavorName + " " + createTime.split(" ")[0]);
			}
		}



		for (int i = 0; i < history.size(); i++) {
			results[i] = history.get(i);

		}


		return results;
	}


}
