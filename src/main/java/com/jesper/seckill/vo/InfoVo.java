package com.jesper.seckill.vo;

import javax.validation.constraints.NotNull;

public class InfoVo {

    @NotNull
    private String nickname;

    private String head;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    @Override
    public String toString() {
        return "InfoVo{" +
                "nickname='" + nickname + '\'' +
                ", head='" + head + '\'' +
                '}';
    }
}
