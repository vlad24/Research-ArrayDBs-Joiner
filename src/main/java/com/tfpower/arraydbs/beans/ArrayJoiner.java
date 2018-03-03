package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.domain.JoinReport;
import org.springframework.stereotype.Component;

/**
 * Created by vlad on 24.01.18.
 */
public interface ArrayJoiner {
    JoinReport join(BiGraph bGraph);
}
