package com.dzics.sanymom.service;

import com.dzics.sanymom.model.ResultDto;
import com.dzics.sanymom.model.request.agv.AutomaticGuidedVehicle;

public interface AgvRobackService {
    ResultDto automaticGuidedVehicle(AutomaticGuidedVehicle automaticGuidedVehicle);

    void handelAgvMessage(String reqId);
}
