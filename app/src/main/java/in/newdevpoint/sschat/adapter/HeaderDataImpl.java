package in.newdevpoint.sschat.adapter;

import androidx.annotation.LayoutRes;


import java.text.SimpleDateFormat;
import java.util.Date;

import in.newdevpoint.sschat.stickyheader.stickyData.HeaderData;

public class HeaderDataImpl implements HeaderData {

	@LayoutRes
	private final int layoutResource;

	private String title;

	public HeaderDataImpl(@LayoutRes int layoutResource, Date date) {
		this.layoutResource = layoutResource;
		this.title = new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@LayoutRes
	@Override
	public int getHeaderLayout() {
		return layoutResource;
	}


}
