package com.dzics.sanymom.service;

import com.dzics.sanymom.model.ResultDto;
import com.dzics.sanymom.model.request.syncuser.SyncMomUser;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
public interface SyncMomUserService {
    ResultDto syncUser(SyncMomUser syncMomUser);
}
