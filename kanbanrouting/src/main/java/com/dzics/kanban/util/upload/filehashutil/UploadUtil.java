package com.dzics.kanban.util.upload.filehashutil;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * @author ZhangChengJun
 * Date 2019/12/20.
 */
@Component
public class UploadUtil {
    private static  final Logger logger = LoggerFactory.getLogger(UploadUtil.class);
    /**
     * 用户上传文件空间名称前缀
     */
    private static String startFileName = "files";


    /**
     * 修改文件默认名，计算文件Hash 和 后缀进行文件重名名
     *
     * @param img
     * @return
     */
    public String getHashName(MultipartFile img) throws IOException, MimeTypeException {
        InputStream inputStream = img.getInputStream();
        String contentType = img.getContentType();
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType jpeg = allTypes.forName(contentType);
        //文件后缀
        String houzhui = jpeg.getExtension();
        String hashName = Etag.stream(inputStream, img.getSize()) + houzhui;
        inputStream.close();
        return hashName;
    }

    public String getHashName(InputStream inputStream, Long size, String type) throws IOException, MimeTypeException {
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType jpeg = allTypes.forName(type);
        //文件后缀
        String houzhui = jpeg.getExtension();
        String hashName = Etag.stream(inputStream, size) + houzhui;
        inputStream.close();
        return hashName;
    }
    /**
     * 修改文件默认名，计算文件Hash 和 后缀进行文件重名名
     *
     * @param img
     * @return
     */
    public String getHashNameNoimg(MultipartFile img) throws IOException, MimeTypeException {
        InputStream inputStream = img.getInputStream();
        String contentType = img.getContentType();
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType jpeg = allTypes.forName(contentType);
        //文件后缀
        String houzhui = jpeg.getExtension();
        String hashName = Etag.stream(inputStream, img.getSize()) + houzhui;
        inputStream.close();
        return hashName;
    }

    /**
     * 修改文件默认名，计算文件Hash 和 后缀进行文件重名名
     *
     * @param img
     * @return
     */
    public String getDirPath(String img) {
        Calendar calendar = Calendar.getInstance();
        String filepath = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        return startFileName + "/" + img + "/" + filepath + "/";
    }
}
