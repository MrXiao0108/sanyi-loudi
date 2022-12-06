package com.dzics.business.service.agv;

import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.agv.search.MomResultSearch;
import com.dzics.common.model.agv.search.SearchDzdcMomSeqenceNo;
import com.dzics.common.model.qrCode.QrCodeParms;
import com.dzics.common.model.request.agv.AgvClickSignal;
import com.dzics.common.model.request.agv.AgvClickSignalConfirmV2;
import com.dzics.common.model.request.dzcheck.DzOrderCheck;
import com.dzics.common.model.response.Result;

public interface AgvService {

    /**
     * 来料信号确认
     *
     * @param clickSignal
     * @return
     */
    Result chlickSignal(AgvClickSignal clickSignal);


    Result checkOrder(DzOrderCheck dzOrderCheck);

    /**
     * 通知机器人扫描FRID
     *
     * @param clickSignal
     * @return
     */
    Result getFrid(AgvClickSignal clickSignal);

    Result chlickOkConfirmMaterialV2(AgvClickSignalConfirmV2 confirm);

    /**
     * 发送写入二维码到底层
     *
     * @param qrCodeParms
     * @return
     */
    Result inputQrCode(QrCodeParms qrCodeParms);

    Result processDistribution(EmptyFrameMovesDzdc emptyFrameMovesDzdc);

    MomResultSearch getSanyMomNextSpecNo(SearchDzdcMomSeqenceNo dzdcMomSeqenceNo);
}
