package com.tfpower.arraydbs.entity;

import java.util.List;

public interface CSVExportable {

    List<String> csvHeaderElements();

    List<String> csvElements();
}
