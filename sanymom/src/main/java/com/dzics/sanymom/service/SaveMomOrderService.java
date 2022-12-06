package com.dzics.sanymom.service;

import com.dzics.sanymom.model.ResultDto;
import com.dzics.sanymom.model.request.sany.IssueOrderInformation;

public interface SaveMomOrderService {
    ResultDto saveMomOrderService(IssueOrderInformation requestHeaderVo,String momParms);
}
