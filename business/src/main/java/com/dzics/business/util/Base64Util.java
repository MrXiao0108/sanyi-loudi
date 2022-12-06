package com.dzics.business.util;

import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.request.UpLoadSize;
import com.dzics.common.util.upload.CompressImg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.IOException;

import static com.dzics.common.service.impl.SysUserServiceImpl.isImage;

@Slf4j
public class Base64Util {

    public static String base64ToString(MultipartFile file) {
        if (!file.isEmpty()) {
            if (file.getSize() < UpLoadSize.SIZE) {
                //   原始文件byte
                try {
                    boolean image = isImage(file.getInputStream());
                    if (image) {
                        byte[] bytesStart = CompressImg.inputStream2byte(file.getInputStream());
                        // 压缩后文件byte
                        byte[] bytesEnd = CompressImg.compressPicForScale(bytesStart, UpLoadSize.SIZE_MIN);
                        BASE64Encoder encoder = new BASE64Encoder ();
                        String head = "data:" + file.getContentType() + ";base64," + encoder.encode(bytesEnd);
                        return head;
                    } else {
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR27);
                    }

                } catch (IOException e) {
                    log.error("上传文件获取文件流异常：{}", e);
                    return null;
                }
            } else {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR26);
            }

        }
        return null;
    }
}
