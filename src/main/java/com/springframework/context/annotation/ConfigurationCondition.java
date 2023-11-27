package com.springframework.context.annotation;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ConfigurationCondition extends Condition {

    /**
     * Return the {@link ConfigurationPhase} in which the condition should be evaluated.
     */
    ConfigurationPhase getConfigurationPhase();


    /**
     * The various configuration phases where the condition could be evaluated.
     */
    enum ConfigurationPhase {

        /**
         * The {@link Condition} should be evaluated as a {@code @Configuration}
         * class is being parsed.
         * <p>If the condition does not match at this point, the {@code @Configuration}
         * class will not be added.
         */
        PARSE_CONFIGURATION,

        /**
         * The {@link Condition} should be evaluated when adding a regular
         * (non {@code @Configuration}) bean. The condition will not prevent
         * {@code @Configuration} classes from being added.
         * <p>At the time that the condition is evaluated, all {@code @Configuration}s
         * will have been parsed.
         */
        REGISTER_BEAN
    }

}

