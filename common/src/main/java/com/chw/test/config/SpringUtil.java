package com.chw.test.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    //当前IOC
    private static ApplicationContext applicationContext;

    //设置当前上下文环境，此方法由spring自动装配
    @Override
    public void setApplicationContext(@Nullable ApplicationContext arg0)
            throws BeansException {
        applicationContext = arg0;
    }
    //获取bean的方法
    public static <T> T getBean(String id, @Nullable Class<T> tClass) {
        return applicationContext.getBean(id,tClass);
    }
}
