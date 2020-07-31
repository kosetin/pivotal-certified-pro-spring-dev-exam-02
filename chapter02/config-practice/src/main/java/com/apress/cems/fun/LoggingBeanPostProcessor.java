package com.apress.cems.fun;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        LoggerFactory.getLogger(bean.getClass())
                .info("\u001b["         // Prefix - see [1]
                        + "0"           // Brightness
                        + ";"           // Separator
                        + "31"          // Red foreground
                        + "m"           // Suffix
                        + "Stage 3-0: Calling common "
                        + this.getClass().getSimpleName()
                        + "'s method on initialization of "
                        + beanName
                        + "\u001b[m "   // Prefix + Suffix to reset color
                );

        return bean;
    }
}
