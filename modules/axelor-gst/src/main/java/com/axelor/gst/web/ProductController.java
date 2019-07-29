package com.axelor.gst.web;


import java.util.List;

import com.axelor.app.AppSettings;

import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;

public class ProductController {


	public void fetchReportData(ActionRequest request, ActionResponse response) {

		Context context = request.getContext();
		String filePath = AppSettings.get().get("file.upload.dir");
		List<String> list = (List<String>) context.get("_ids");
		if (list != null) {
			String lists = list.toString();
			context.put("listId", lists.substring(1, lists.length() - 1));
		} else {
			Long ids = (Long) context.get("id");
			if(ids==null) {
				response.setError("Please Select a Product!!");
			}else {
			context.put("listId", ids.toString());
			}
			}
		context.put("filePath", filePath);

	}
}
