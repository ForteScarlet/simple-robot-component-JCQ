package com.simplerobot.component.jcq;

import org.meowy.cqp.jcq.entity.CoolQ;

import java.time.Instant;

/**
 * JCQMainApplication Java启动类，可参见{@link JCQMain}
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public abstract class JCQMainApplication extends JCQMain {
    public JCQMainApplication(CoolQ cq) {
        super(cq);
    }
}
