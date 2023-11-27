package com.springframework.core.env;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractEnvironment implements ConfigurableEnvironment {
    private final MutablePropertySources propertySources = new MutablePropertySources();
    private final ConfigurablePropertyResolver propertyResolver;

    public AbstractEnvironment() {
        this.propertyResolver = new PropertySourcesPropertyResolver(this.propertySources);
        this.customizePropertySources(this.propertySources);
    }

    protected void customizePropertySources(MutablePropertySources propertySources) {
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return this.propertyResolver.resolveRequiredPlaceholders(text);
    }
}
