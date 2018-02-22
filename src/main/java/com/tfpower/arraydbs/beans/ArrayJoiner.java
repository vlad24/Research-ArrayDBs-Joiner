package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.domain.JoinReport;

/**
 * Created by vlad on 24.01.18.
 */
public interface ArrayJoiner {
    JoinReport join(BGraph bGraph);
}
