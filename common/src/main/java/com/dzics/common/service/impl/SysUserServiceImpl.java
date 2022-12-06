package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysRoleMapper;
import com.dzics.common.dao.SysUserMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.SysDepart;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.PutUserInfoVo;
import com.dzics.common.model.request.ReqUploadBase64;
import com.dzics.common.model.request.UpLoadSize;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.UserListRes;
import com.dzics.common.service.SysDepartService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.upload.CompressImg;
import com.dzics.common.util.upload.fileHashutil.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//import sun.misc.BASE64Encoder;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserServiceDao {

    @Autowired
    private UploadUtil uploadUtil;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysDepartService sysDepartService;

    @Override
    public SysUser getByUserName(String sub) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>();
        queryWrapper.eq("username", sub);
        SysUser one = this.getOne(queryWrapper);
        if (one == null) {
            throw new CustomException(CustomExceptionType.USER_IS_NULL, CustomExceptionType.USER_IS_NULL.getTypeDesc() + ":" + sub);
        }
        SysDepart byId = sysDepartService.getById(one.getAffiliationDepartId());
        one.setCode(byId.getOrgCode());
        return one;
    }

    @Override
    public Integer getCountByOrgCode(String orgCode) {
        QueryWrapper<SysUser> wpUs = new QueryWrapper<>();
        wpUs.eq("org_code", orgCode);
        return baseMapper.selectCount(wpUs);
    }

    @Override
    public List<UserListRes> listUserOrgCode(String field, String type, String useOrgCode, String realname, String username, Integer status, Date createTime, Date endTime) {
        return baseMapper.listUserOrgCode(field, type, useOrgCode, realname, username, status, createTime, endTime);
    }


    @Override
    public Result put(String sub, PutUserInfoVo putUserInfoVo) {
        SysUser byUserName = getByUserName(sub);
        byUserName.setRealname(putUserInfoVo.getRealname());
        byUserName.setPhone(putUserInfoVo.getPhone());
        byUserName.setEmail(putUserInfoVo.getRealname());
        byUserName.setSex(putUserInfoVo.getSex());
        this.updateById(byUserName);
        return new Result(CustomExceptionType.OK, byUserName);
    }


    @Override
    public Result putAvatar(String sub, MultipartFile file) {
        if (!file.isEmpty()) {
            if (file.getSize() < UpLoadSize.SIZE) {
                //   原始文件byte
                try {
                    boolean image = isImage(file.getInputStream());
                    if (image) {
                        byte[] bytesStart = CompressImg.inputStream2byte(file.getInputStream());
                        // 压缩后文件byte
                        byte[] bytesEnd = CompressImg.compressPicForScale(bytesStart, UpLoadSize.SIZE_MIN);
                        BASE64Encoder encoder = new BASE64Encoder();
                        SysUser byUserName = this.getByUserName(sub);
                        String head = "data:" + file.getContentType() + ";base64," + encoder.encode(bytesEnd);
                        byUserName.setAvatar(head);
                        this.updateById(byUserName);
                        return new Result(CustomExceptionType.OK, byUserName);
                    } else {
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR27);
                    }

                } catch (IOException e) {
                    log.error("上传文件获取文件流异常：{}", e);
                    return new Result(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR0);
                }
            } else {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR26);
            }
        }
        //文件为空
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_52);

    }

    @Override
    public Result putUpload(String sub, MultipartFile[] multipartFiles, String address, String parentPath) {
        if (multipartFiles != null) {
            String errorMsg = "";
            List<String> addressUrl = new ArrayList<>();
            for (MultipartFile file : multipartFiles) {
                if (file.getSize() < UpLoadSize.SIZE) {
                    //   原始文件byte
                    try {
                        boolean image = isImage(file.getInputStream());
                        if (image) {
                            byte[] bytesStart = CompressImg.inputStream2byte(file.getInputStream());
//            压缩后文件byte
                            byte[] bytesEnd = CompressImg.compressPicForScale(bytesStart, UpLoadSize.SIZE_MIN);
                            InputStream inputStreamStart = CompressImg.byte2InputStream(bytesEnd);
                            Long size = Long.valueOf(bytesEnd.length);
//            上传文件名
                            String contentType = file.getContentType();
//                        文件名
                            String fileHashName = uploadUtil.getHashName(inputStreamStart, size, contentType);
//                       上传文件地址
                            String parpath = uploadUtil.getDirPath(contentType);
//                        文件磁盘地址
                            String dirPath = parentPath + parpath;
//            文件上传流
                            InputStream stream = CompressImg.byte2InputStream(bytesEnd);
                            File filePath = new File(dirPath);
                            if (!filePath.exists()) {
                                //创建文件夹
                                filePath.mkdirs();
                            }
                            //创建目标文件
                            String url = dirPath + fileHashName;
                            File targetFile = new File(url);
                            FileOutputStream fos = new FileOutputStream(targetFile);
                            try {
//                       写入目标文件
                                byte[] buffer = new byte[1024 * 1024];
                                int byteRead = 0;
                                while ((byteRead = stream.read(buffer)) != -1) {
                                    fos.write(buffer, 0, byteRead);
                                    fos.flush();
                                }
                                fos.close();
                                stream.close();
                            } catch (Throwable e) {
                                fos.close();
                                stream.close();
                            }
                            String getUrl = address + parpath + fileHashName;
                            addressUrl.add(getUrl);
                        } else {
                            errorMsg = errorMsg + "存在文件类型错误|";
                            continue;
                        }

                    } catch (IOException e) {
                        errorMsg = errorMsg + "稍后再试|";
                        log.error("上传文件获取文件流异常：{}", e);
                        continue;
                    } catch (MimeTypeException e) {
                        errorMsg = errorMsg + "文件类型错误|";
                        log.error("上传文件获取文件类型异常：{}", e);
                        continue;
                    }
                } else {
                    errorMsg = errorMsg + "文件过大";
                    log.error("文件过大");
                    continue;
                }
            }
            if (org.springframework.util.StringUtils.isEmpty(errorMsg)) {
                return Result.ok(addressUrl);
            } else {
                Result result = new Result(CustomExceptionType.OK, errorMsg);
                result.setData(addressUrl);
                return result;
            }
        }
        //文件为空
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_52);
    }


    @Override
    public String getUserRoleName(Long id) {
        //查询用户角色
        List<String> roleNameList = sysRoleMapper.getRoleName(id);
        if (roleNameList.size() > 0) {
            String join = StringUtils.join(roleNameList, ',');
            return join;
        }
        return null;
    }

    @Override
    public Long listUsername(String username) {
        return baseMapper.listUsername(username);
    }

    public static boolean isImage(InputStream imageFile) {
        Image img = null;
        try {
            img = ImageIO.read(imageFile);
            if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            img = null;
        }
    }

    @Override
    public String getUserOrgCode(String sub) {
        SysUser byUserName = getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            return null;
        } else {
            return byUserName.getUseOrgCode();
        }

    }

    @Override
    public Result putAvatarBase64(String sub, ReqUploadBase64 files, String address, String parentPath) {
        List<String> imgUrl = new ArrayList<>();
        List<String> imgBase64 = files.getImgBase64();
        if (CollectionUtils.isNotEmpty(imgBase64)) {
            for (String base64 : imgBase64) {
//                文件名
                String fileHashName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
//                       上传文件地址
                String parpath = uploadUtil.getDirPathBase64("png");
//                        文件磁盘地址
                String dirPath = parentPath + parpath;
//            文件上传流
                File filePath = new File(dirPath);
                if (!filePath.exists()) {
                    //创建文件夹
                    filePath.mkdirs();
                }
                InputStream stream = null;
                FileOutputStream fos = null;
                try {
                    String[] urlXXX = base64.split(",");
                    String img = urlXXX[1];
                    byte[] imgByte = new byte[0];
                    imgByte = new BASE64Decoder().decodeBuffer(img);
                    stream = CompressImg.byte2InputStream(imgByte);
                    //创建目标文件
                    String url = dirPath + fileHashName;
                    File targetFile = new File(url);
                    fos = new FileOutputStream(targetFile);
//                       写入目标文件
                    byte[] buffer = new byte[1024 * 1024];
                    int byteRead = 0;
                    while ((byteRead = stream.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteRead);
                        fos.flush();
                    }
                } catch (Throwable throwable) {
                    log.error("上传文件错误：{}", throwable.getMessage(), throwable);
                } finally {
                    try {
                        fos.close();
                        stream.close();
                    } catch (IOException e) {
                        log.error("关闭流异常：{}", e.getMessage(), e);
                    }
                }
                String getUrl = address + parpath + fileHashName;
                imgUrl.add(getUrl);
            }
            return Result.OK(imgUrl);
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0);
    }


    /**
     * 对字节数组字符串进行Base64解码并生成图片
     *
     * @param imgStr
     * @param imgFilePath
     * @return
     */
    public static boolean GenerateImage(String imgStr, String imgFilePath) {
        OutputStream out = null;
        if (imgStr == null) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] bytes = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("管理文件流异常：{}", e.getMessage(), e);
                }
            }
        }
    }
}
