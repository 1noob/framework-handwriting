package com.springframework.context.event;

import com.springframework.context.ApplicationEvent;
import com.springframework.context.ApplicationListener;
import com.springframework.core.Ordered;

public interface GenericApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {}
