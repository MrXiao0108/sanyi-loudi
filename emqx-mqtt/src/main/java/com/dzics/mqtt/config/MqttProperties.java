package com.dzics.mqtt.config;

import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/6/30.
 * @since
 */
@Data
public class MqttProperties {

    public static final String serverUrlType = "ssl";
    /**
     * 设置客户端和服务器是否应该在重新启动和重新连接时记住状态。
     * 如果设置为false，客户端和服务器都将在客户端、服务器和连接重新启动时保持状态。
     * 由于状态是维护的：即使客户端、服务器或连接重新启动，消息传递也将可靠地满足指定的 QOS。
     * 服务器将订阅视为持久的。如果设置为true，客户端和服务器将不会在客户端、服务器或连接重新启动时保持状态。
     * 这意味着 如果客户端、服务器或连接重新启动，则无法维持到指定 QOS 的消息传递
     * 服务器会将订阅视为非持久订阅
     * 设置为 True 以启用 cleanSession
     */
    public static final Boolean cleanSession = false;
    /**
     * 设置连接超时值。
     * 该值以秒为单位，定义  客户端将等待与MQTT 服务器建立网络连接的最大时间间隔。
     * 默认超时为 30 秒。值 0 禁用超时处理，这意味着客户端将等待网络连接成功或失败。
     * 超时值，以秒为单位。它必须>0；
     */
    public static final Integer connectionTimeout = 30;

    /**
     * 设置“保持活动”间隔。这个值以秒为单位，定义了发送或接收消息之间的最大时间间隔。
     * 它使 客户端能够检测服务器是否不再可用，而无需等待  TCP/IP 超时。
     * 客户端将确保在每个保活期内至少有一条消息通过网络传输。
     * 在该时间段内没有数据相关消息时，客户端发送一个非常小的“ping”消息，服务器将确认该消息。
     * 值为 0 将禁用客户端中的 keepalive 处理。 *默认值为 60 秒
     * 间隔，以秒为单位，必须 >= 0
     */
    public static final Integer keepAliveInterval = 30;
    /**
     * 设置客户端是否会自动尝试重新连接到服务器
     * 连接丢失。
     * 如果设置为false，客户端将不会尝试在连接丢失的情况下自动重新连接到服务器。
     * 如果设置为true，则在该事件中连接丢失，客户端将 * 尝试重新连接到服务器。
     * 它最初会在尝试重新连接之前等待 1 秒，
     * 对于每次失败的重新连接尝试，延迟将 * 加倍，直到 2 分钟，此时延迟将保持在 2 * 分钟。
     * 如果设置为 True，将启用自动重新连接
     */
    public static final Boolean automaticReconnect = true;

}
