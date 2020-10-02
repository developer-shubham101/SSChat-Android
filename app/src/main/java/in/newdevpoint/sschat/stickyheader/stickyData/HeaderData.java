package in.newdevpoint.sschat.stickyheader.stickyData;


import androidx.annotation.LayoutRes;

import in.newdevpoint.sschat.model.StickyMainData;


public interface HeaderData extends StickyMainData {
	@LayoutRes
	int getHeaderLayout();

}
