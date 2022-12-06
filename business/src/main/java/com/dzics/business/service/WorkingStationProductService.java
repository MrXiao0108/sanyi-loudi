package com.dzics.business.service;

import com.dzics.common.model.request.locationartifacts.AddLocationArtifactsVo;
import com.dzics.common.model.request.locationartifacts.LocationArtifactsVo;
import com.dzics.common.model.request.locationartifacts.PutLocationArtifactsVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.locationartifacts.GetLocationArtifactsByIdDo;

public interface WorkingStationProductService {
    Result locationArtifactsList(LocationArtifactsVo locationArtifactsVo, String sub);

    Result add(AddLocationArtifactsVo addLocationArtifactsVo, String sub);

    Result<GetLocationArtifactsByIdDo> selEditProcedureProduct(String orderId, String lineId, String workStationProductId, String sub);

    Result updateProcedureProduct(PutLocationArtifactsVo putLocationArtifactsVo, String sub);

    Result delWorkStationProductId(String workStationProductId, String sub);
}
