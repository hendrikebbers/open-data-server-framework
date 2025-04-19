package com.openelements.data.data;

import java.util.List;

public interface DataProvider<ENTITY> {

    List<ENTITY> getPage(int page, int pageSize);
    
    List<ENTITY> getAll();

    long getCount();

}
