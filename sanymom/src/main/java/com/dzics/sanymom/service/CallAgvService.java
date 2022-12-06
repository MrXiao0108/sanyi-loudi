package com.dzics.sanymom.service;

import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.response.Result;

public interface CallAgvService {
    Result<String> callAgv(EmptyFrameMovesDzdc emptyFrameMovesDzdc);


}
