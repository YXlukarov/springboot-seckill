package com.jesper.seckill.redis;

/**
 * Created by jiangyunxiong on 2018/5/21.
 */
public abstract class BasePrefix implements KeyPrefix {

    // 存入Redis的过期时间
    private int expireSeconds;
    // 参数类类名
    private String prefix;

    public BasePrefix(String prefix){
        this(0, prefix);//默认0代表永不过期
    }

    public BasePrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName(); //拿到参数类类名
        return className + ":" + prefix;
    }
}
