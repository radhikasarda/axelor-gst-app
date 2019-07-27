package com.axelor.gst.web;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.app.AppSettings;
import com.axelor.axelor.gst.db.Product;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;

public class ProductController {

	public void fetchGstRate(ActionRequest request, ActionResponse response) {

		Product product = request.getContext().asType(Product.class);
		BigDecimal gstRate = BigDecimal.ZERO;
		gstRate = product.getCategory().getGstRate();
		product.setGstRate(gstRate);
		response.setValue("gstRate", gstRate);

	}

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
