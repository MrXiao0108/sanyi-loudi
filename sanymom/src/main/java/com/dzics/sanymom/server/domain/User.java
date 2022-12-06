package com.dzics.sanymom.server.domain;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * User Domain Object
 */
@Slf4j
public class User {
    public static final AttributeKey<User> USER_ATTRIBUTE_KEY = AttributeKey.newInstance("USER");

    @Getter
    private final String username;
    private final Channel channel;

    private User(String username, Channel channel) {
        this.username = username;
        this.channel = channel;
    }

    public static User of(@NonNull String loginCommand, @NonNull Channel channel) {
        if (loginCommand.startsWith("login ")) {
            return new User(loginCommand.trim().substring("login ".length()), channel);
        }

        throw new IllegalArgumentException("loginCommand [" + loginCommand + "] can not be accepted");
    }

    public void login(ChannelRepository channelRepository, Channel channel) {
        log.info("用户 {} 登录", this.username);
        channel.attr(USER_ATTRIBUTE_KEY).set(this);
        channelRepository.put(this.username, channel);
    }

    public void logout(ChannelRepository channelRepository, Channel channel) {
        log.info("用户 {} 登出", this.username);
        channel.attr(USER_ATTRIBUTE_KEY).getAndSet(null);
        channelRepository.remove(this.username);
        channel.close();
        channel.closeFuture();
        log.info("用户 {} 登出成功,countSize:{}", this.username, channelRepository.size());
    }

    public static User current(Channel channel) {
        User user = channel.attr(USER_ATTRIBUTE_KEY).get();
        if (user == null) {
            throw new UserLoggedOutException();
        }
        return user;
    }

    public void tell(Channel targetChannel, @NonNull String username, @NonNull String message) {
        if (targetChannel != null) {
            targetChannel.write(this.username);
            targetChannel.write(">");
            targetChannel.writeAndFlush(message + "\n\r");
            this.channel.writeAndFlush("The message was sent to [" + username + "] successfully.\r\n");
        } else {
            this.channel.writeAndFlush("No user named with [" + username + "].\r\n");
        }
    }
}