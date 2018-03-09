package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.JoinReport;

/**
 * Created by vlad on 24.01.18.
 */
public interface ArrayJoiner {
    JoinReport join(BiGraph bGraph);
}
