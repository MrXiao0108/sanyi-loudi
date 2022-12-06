package com.dzics.sanymom.service;

import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.response.Result;

public  interface AgvService<T> {
    /**
     * 移动AGV
     *
     * @param frameMoves
     * @return
     */
    Result<T> moveAgv(EmptyFrameMovesDzdc frameMoves);

}
