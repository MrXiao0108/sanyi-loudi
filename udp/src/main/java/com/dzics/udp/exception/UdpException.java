package com.dzics.udp.exception;

/**
 * @author ZhangChengJun
 * Date 2021/3/19.
 * @since
 */
public class UdpException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1632386746870143059L;
	public UdpException(String errMsg) {
		super(errMsg);
	}
	public UdpException(Throwable e) {
		super(e);
	}
	public UdpException(String message, Throwable cause) {
        super(message, cause);
    }
}
